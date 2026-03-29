<template>
  <div class="page-wrap app-page">
        <div class="layout-grid">
      <el-card shadow="hover" class="tree-card app-card">
        <template #header>
          <div class="card-head">
            <span>组织树</span>
            <el-button text type="primary" @click="loadOrgTree">刷新</el-button>
          </div>
        </template>

        <div class="tree-tools">
          <el-input v-model="treeKeyword" placeholder="搜索单位名称或编码" clearable />
        </div>

        <el-scrollbar height="650px">
          <el-tree
            ref="treeRef"
            :data="orgTree"
            node-key="id"
            :props="treeProps"
            default-expand-all
            highlight-current
            :filter-node-method="filterTreeNode"
            @node-click="onOrgNodeClick"
          >
            <template #default="{ data }">
              <div class="tree-node">
                <span class="tree-name">{{ data.name }}</span>
                <el-tag size="small" type="info">{{ data.code || "ROOT" }}</el-tag>
              </div>
            </template>
          </el-tree>
        </el-scrollbar>
      </el-card>

      <div class="content-col">
        <el-card shadow="hover" class="search-card app-card">
          <template #header>
            <div class="card-head simple-head">
              <span>筛选条件</span>
            </div>
          </template>

          <el-form :model="searchForm" inline class="search-form">
            <el-form-item label="关键字">
              <el-input
                v-model="searchForm.keyword"
                placeholder="账号 / 姓名 / 手机 / 邮箱"
                clearable
                style="width: 220px"
                @keyup.enter="doSearch"
              />
            </el-form-item>

            <el-form-item label="状态">
              <el-select
                v-model="searchForm.status"
                clearable
                placeholder="全部"
                style="width: 120px"
              >
                <el-option label="启用" :value="1" />
                <el-option label="禁用" :value="0" />
              </el-select>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="doSearch">查询</el-button>
              <el-button @click="resetSearch">重置</el-button>
              <el-button type="success" @click="openCreate">新增用户</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="hover" class="table-card app-card">
          <template #header>
            <div class="card-head">
              <span>用户列表</span>
              <div class="head-actions">
                <el-button text type="primary" @click="loadPage">刷新列表</el-button>
              </div>
            </div>
          </template>

          <el-table
            :data="tableData"
            border
            v-loading="loading"
            class="user-table"
            table-layout="fixed"
          >
            <el-table-column
              prop="username"
              label="账号"
              width="130"
              show-overflow-tooltip
            />
            <el-table-column
              prop="realName"
              label="姓名"
              width="100"
              show-overflow-tooltip
            />
            <el-table-column
              prop="unitName"
              label="所属单位"
              min-width="150"
              show-overflow-tooltip
            />
            <el-table-column label="角色" width="170">
              <template #default="{ row }">
                <div class="role-cell">
                  <el-tag
                    v-for="r in row.roles || []"
                    :key="r"
                    size="small"
                    effect="plain"
                    class="role-tag"
                  >
                    {{ r }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="phone"
              label="手机号"
              width="130"
              show-overflow-tooltip
            />
            <el-table-column
              prop="email"
              label="邮箱"
              width="180"
              show-overflow-tooltip
            />
            <el-table-column label="状态" width="86" align="center">
              <template #default="{ row }">
                <el-tag size="small" :type="row.status === 1 ? 'success' : 'danger'">
                  {{ row.status === 1 ? "启用" : "禁用" }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="lastLoginAt"
              label="最后登录"
              width="160"
              show-overflow-tooltip
            />
            <el-table-column label="操作" width="240" fixed="right" align="center">
              <template #default="{ row }">
                <div class="action-row">
                  <el-button class="action-btn" size="small" @click="openEdit(row)">
                    编辑
                  </el-button>

                  <el-button
                    class="action-btn"
                    size="small"
                    type="primary"
                    plain
                    @click="openRoleDialog(row)"
                  >
                    角色
                  </el-button>

                  <el-dropdown trigger="click" @command="(cmd: string) => handleActionCommand(cmd, row)">
                    <el-button class="action-btn" size="small" type="info" plain>
                      更多
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="reset">重置密码</el-dropdown-item>
                        <el-dropdown-item :command="row.status === 1 ? 'disable' : 'enable'">
                          {{ row.status === 1 ? "禁用用户" : "启用用户" }}
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <div class="pager-wrap">
            <el-pagination
              background
              layout="total, sizes, prev, pager, next"
              :total="pager.total"
              :page-size="pager.pageSize"
              :current-page="pager.pageNo"
              :page-sizes="[10, 20, 50]"
              @size-change="onSizeChange"
              @current-change="onPageChange"
            />
          </div>
        </el-card>
      </div>
    </div>

    <el-dialog
      v-model="editDialog.visible"
      :title="editDialog.mode === 'create' ? '新增用户' : '编辑用户'"
      width="680px"
    >
      <el-form
        ref="editFormRef"
        :model="editDialog.form"
        :rules="editRules"
        label-width="90px"
      >
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="账号" prop="username">
              <el-input
                v-model="editDialog.form.username"
                :disabled="editDialog.mode !== 'create'"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12" v-if="editDialog.mode === 'create'">
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="editDialog.form.password"
                show-password
                placeholder="默认建议 123456"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="editDialog.form.realName" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="单位" prop="unitId">
              <el-select
                v-model="editDialog.form.unitId"
                filterable
                clearable
                placeholder="请选择单位"
                style="width: 100%"
              >
                <el-option
                  v-for="u in unitFlatOptions"
                  :key="u.id"
                  :label="u.label"
                  :value="u.id"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="editDialog.form.phone" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="editDialog.form.email" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="editDialog.form.status">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>

          <el-col :span="24" v-if="editDialog.mode === 'create'">
            <el-form-item label="角色" prop="roleCodes">
              <el-select
                v-model="editDialog.form.roleCodes"
                multiple
                filterable
                placeholder="请选择角色"
                style="width: 100%"
              >
                <el-option
                  v-for="r in roles"
                  :key="r.roleCode"
                  :label="r.roleName"
                  :value="r.roleCode"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="editDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="editDialog.saving" @click="submitEdit">
          保存
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialog.visible" title="分配角色" width="560px">
      <el-form label-width="90px">
        <el-form-item label="用户">
          <el-input :model-value="roleDialog.userLabel" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-select
            v-model="roleDialog.roleCodes"
            multiple
            filterable
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option
              v-for="r in roles"
              :key="r.roleCode"
              :label="r.roleName"
              :value="r.roleCode"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="roleDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="roleDialog.saving" @click="submitRoles">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from "vue";
import type { FormInstance, FormRules, TreeInstance } from "element-plus";
import { ElMessage, ElMessageBox } from "element-plus";
import { apiRoles, type RoleOption } from "@/api/rbac";
import { apiOrgTree, type OrgTreeNode } from "@/api/org";
import {
  apiUserCreate,
  apiUserDisable,
  apiUserEnable,
  apiUserPage,
  apiUserResetPassword,
  apiUserSetRoles,
  apiUserUpdate,
  type UserVO,
} from "@/api/userAdmin";

const treeRef = ref<TreeInstance>();
const editFormRef = ref<FormInstance>();

const loading = ref(false);
const roles = ref<RoleOption[]>([]);
const orgTree = ref<OrgTreeNode[]>([]);
const tableData = ref<UserVO[]>([]);
const currentOrgId = ref<number | null>(null);
const currentOrgName = ref("");
const treeKeyword = ref("");

const pager = reactive({ pageNo: 1, pageSize: 10, total: 0 });
const searchForm = reactive<{ keyword: string; status: number | null }>({
  keyword: "",
  status: null,
});

const treeProps = { label: "name", children: "children" };

const editDialog = reactive({
  visible: false,
  mode: "create" as "create" | "edit",
  saving: false,
  form: {
    id: undefined as number | undefined,
    username: "",
    password: "123456",
    realName: "",
    phone: "",
    email: "",
    unitId: undefined as number | undefined,
    status: 1,
    roleCodes: [] as string[],
  },
});

const roleDialog = reactive({
  visible: false,
  saving: false,
  userId: 0,
  userLabel: "",
  roleCodes: [] as string[],
});

const editRules: FormRules = {
  username: [{ required: true, message: "请输入账号", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  realName: [{ required: true, message: "请输入姓名", trigger: "blur" }],
  unitId: [{ required: true, message: "请选择单位", trigger: "change" }],
  roleCodes: [{ required: true, message: "请选择至少一个角色", trigger: "change" }],
};


watch(treeKeyword, (val) => treeRef.value?.filter(val));

const unitFlatOptions = computed(() => {
  const list: Array<{ id: number; label: string }> = [];
  const walk = (nodes: OrgTreeNode[], prefix = "") => {
    nodes.forEach((node) => {
      const label = prefix ? `${prefix} / ${node.name}` : node.name;
      list.push({ id: node.id, label });
      if (node.children?.length) walk(node.children, label);
    });
  };
  walk(orgTree.value);
  return list;
});

function filterTreeNode(value: string, data: OrgTreeNode) {
  if (!value) return true;
  return (data.name || "").includes(value) || (data.code || "").includes(value);
}

function onOrgNodeClick(data: OrgTreeNode) {
  currentOrgId.value = data.id;
  currentOrgName.value = data.name;
  pager.pageNo = 1;
  loadPage();
}

function doSearch() {
  pager.pageNo = 1;
  loadPage();
}

function resetSearch() {
  searchForm.keyword = "";
  searchForm.status = null;
  currentOrgId.value = null;
  currentOrgName.value = "";
  loadPage();
}

function onPageChange(page: number) {
  pager.pageNo = page;
  loadPage();
}

function onSizeChange(size: number) {
  pager.pageSize = size;
  pager.pageNo = 1;
  loadPage();
}

function resetEditForm() {
  editDialog.form.id = undefined;
  editDialog.form.username = "";
  editDialog.form.password = "123456";
  editDialog.form.realName = "";
  editDialog.form.phone = "";
  editDialog.form.email = "";
  editDialog.form.unitId = currentOrgId.value ?? undefined;
  editDialog.form.status = 1;
  editDialog.form.roleCodes = [];
}

function openCreate() {
  editDialog.mode = "create";
  resetEditForm();
  editDialog.visible = true;
}

function openEdit(row: UserVO) {
  editDialog.mode = "edit";
  editDialog.visible = true;
  editDialog.form.id = row.id;
  editDialog.form.username = row.username;
  editDialog.form.password = "123456";
  editDialog.form.realName = row.realName;
  editDialog.form.phone = row.phone || "";
  editDialog.form.email = row.email || "";
  editDialog.form.unitId = row.unitId || undefined;
  editDialog.form.status = row.status;
  editDialog.form.roleCodes = [...(row.roles || [])];
}

function openRoleDialog(row: UserVO) {
  roleDialog.userId = row.id;
  roleDialog.userLabel = `${row.realName}（${row.username}）`;
  roleDialog.roleCodes = [...(row.roles || [])];
  roleDialog.visible = true;
}

async function submitEdit() {
  await editFormRef.value?.validate();
  editDialog.saving = true;
  try {
    if (editDialog.mode === "create") {
      await apiUserCreate({
        username: editDialog.form.username,
        password: editDialog.form.password,
        realName: editDialog.form.realName,
        phone: editDialog.form.phone,
        email: editDialog.form.email,
        unitId: editDialog.form.unitId,
        status: editDialog.form.status,
        roleCodes: editDialog.form.roleCodes,
      });
      ElMessage.success("用户创建成功");
    } else {
      await apiUserUpdate({
        id: Number(editDialog.form.id),
        realName: editDialog.form.realName,
        phone: editDialog.form.phone,
        email: editDialog.form.email,
        unitId: editDialog.form.unitId,
        status: editDialog.form.status,
      });
      ElMessage.success("用户更新成功");
    }
    editDialog.visible = false;
    loadPage();
  } finally {
    editDialog.saving = false;
  }
}

async function submitRoles() {
  if (!roleDialog.roleCodes.length) {
    ElMessage.warning("请至少选择一个角色");
    return;
  }
  roleDialog.saving = true;
  try {
    await apiUserSetRoles(roleDialog.userId, roleDialog.roleCodes);
    ElMessage.success("角色分配成功");
    roleDialog.visible = false;
    loadPage();
  } finally {
    roleDialog.saving = false;
  }
}

async function toggleStatus(row: UserVO) {
  const enable = row.status !== 1;
  await ElMessageBox.confirm(
    `确认${enable ? "启用" : "禁用"}用户“${row.realName}”吗？`,
    "提示",
    { type: "warning" }
  );
  if (enable) {
    await apiUserEnable(row.id);
    ElMessage.success("用户已启用");
  } else {
    await apiUserDisable(row.id);
    ElMessage.success("用户已禁用");
  }
  loadPage();
}

async function resetPassword(row: UserVO) {
  await ElMessageBox.confirm(
    `确认将“${row.realName}”的密码重置为 123456 吗？`,
    "重置密码",
    { type: "warning" }
  );
  await apiUserResetPassword(row.id, "123456");
  ElMessage.success("密码已重置为 123456");
}

function handleActionCommand(command: string, row: UserVO) {
  if (command === "reset") {
    resetPassword(row);
  } else if (command === "disable" || command === "enable") {
    toggleStatus(row);
  }
}

async function loadBaseData() {
  const [roleList, tree] = await Promise.all([apiRoles(), apiOrgTree()]);
  roles.value = roleList;
  orgTree.value = tree;
}

async function loadOrgTree() {
  orgTree.value = await apiOrgTree();
}

async function loadPage() {
  loading.value = true;
  try {
    const res = await apiUserPage({
      pageNo: pager.pageNo,
      pageSize: pager.pageSize,
      unitId: currentOrgId.value,
      keyword: searchForm.keyword || undefined,
      status: searchForm.status,
    });
    tableData.value = res.records || [];
    pager.total = res.total || 0;
  } finally {
    loading.value = false;
  }
}

loadBaseData().then(loadPage);
</script>

<style scoped>
.hero-card {
  background: linear-gradient(135deg, #2563eb, #4f46e5);
  color: #fff;
  border-radius: 18px;
  padding: 22px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  box-shadow: 0 12px 30px rgba(79, 70, 229, 0.2);
}

.page-title {
  font-size: 26px;
  font-weight: 700;
}

.page-subtitle {
  margin-top: 8px;
  font-size: 14px;
  opacity: 0.92;
}

.toolbar-right {
  display: flex;
  gap: 10px;
}

.stats-row {
  margin-top: 16px;
}

.stat-card {
  border-radius: 16px;
}

.stat-label {
  color: #64748b;
  font-size: 14px;
}

.stat-value {
  margin-top: 10px;
  font-size: 30px;
  font-weight: 700;
  color: #111827;
}

.success-card {
  background: linear-gradient(180deg, #f6fffa 0%, #ecfdf5 100%);
}

.danger-card {
  background: linear-gradient(180deg, #fff8f8 0%, #fef2f2 100%);
}

.layout-grid {
  margin-top: 16px;
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 16px;
}

.tree-card,
.search-card,
.table-card {
  border-radius: 16px;
  border: none;
}

.tree-card {
  min-height: 760px;
}

.content-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.table-card {
  flex: 1;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  font-weight: 700;
}

.simple-head {
  justify-content: flex-start;
}

.head-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.tree-tools {
  margin-bottom: 12px;
}

.tree-node {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding-right: 8px;
}

.tree-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 10px;
}

.user-table {
  margin-top: 6px;
  width: 100%;
}

.role-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.role-tag {
  margin: 0;
}

.action-row {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  flex-wrap: nowrap;
}

.action-btn {
  min-width: 60px;
}

.action-row :deep(.el-button),
.action-row :deep(.el-dropdown .el-button) {
  height: 30px;
  padding: 0 12px;
}

.user-table :deep(.el-table__cell) {
  vertical-align: middle;
}

.pager-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

@media (max-width: 1080px) {
  .layout-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .hero-card {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
