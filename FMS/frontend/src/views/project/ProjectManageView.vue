<template>
  <div class="page-wrap app-page">
    <el-card shadow="hover" class="segment-card app-card" v-if="showTeacherTabs || canAuditAny">
      <div class="segment-wrap">
        <div class="segment-title">工作视角</div>
        <div class="segment-list">
          <div
            v-for="item in scopeTabs"
            :key="item.key"
            class="segment-item"
            :class="{ active: activeScope === item.key }"
            @click="changeScope(item.key)"
          >
            <div class="segment-name">{{ item.label }}</div>
          </div>
        </div>
      </div>
    </el-card>

    <el-card shadow="hover" class="search-card app-card">
      <template #header>
        <div class="card-head simple-head">
          <span>项目筛选</span>
          <div class="head-right">
            <el-button v-if="canCreate" type="primary" @click="openCreate">新增项目草稿</el-button>
          </div>
        </div>
      </template>

      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键字">
          <el-input
            v-model="searchForm.keyword"
            placeholder="项目编号 / 项目名称 / 负责人"
            clearable
            style="width: 280px"
            @keyup.enter="doSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" clearable placeholder="全部" style="width: 180px">
            <el-option label="草稿" :value="0" />
            <el-option label="待二级单位审批" :value="1" />
            <el-option label="待财务处审批" :value="2" />
            <el-option label="立项通过" :value="3" />
            <el-option label="已驳回" :value="4" />
            <el-option label="已结题" :value="5" />
            <el-option label="停用" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="canAuditAny">
          <el-checkbox v-model="searchForm.todoOnly">只看待我审批</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover" class="table-card app-card">
      <template #header>
        <div class="card-head">
          <span>项目列表</span>
          <div class="table-head-tools">
            <el-button text type="primary" @click="loadPage">刷新列表</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" border v-loading="loading" table-layout="fixed" class="project-table">
        <el-table-column label="项目基础信息" min-width="300">
          <template #default="{ row }">
            <div class="project-name-cell">
              <div class="project-main-line">
                <b>{{ row.projectName }}</b>
                <el-tag size="small" effect="plain">{{ row.projectCode }}</el-tag>
              </div>
              <div class="project-sub-line">
                <span>类型：{{ row.projectType || '未填写' }}</span>
                <span>负责人：{{ row.principalName || '-' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="unitName" label="所属单位" width="150" show-overflow-tooltip />
        <el-table-column label="执行周期" width="220">
          <template #default="{ row }">{{ row.startDate || '-' }} ~ {{ row.endDate || '-' }}</template>
        </el-table-column>
        <el-table-column label="预算总额" width="140" align="right">
          <template #default="{ row }">{{ money(row.totalBudget) }}</template>
        </el-table-column>
        <el-table-column label="成员数" width="90" align="center">
          <template #default="{ row }"><el-tag size="small" effect="plain">{{ row.memberCount || 0 }}</el-tag></template>
        </el-table-column>
        <el-table-column label="审批进度" width="210">
          <template #default="{ row }">
            <div class="step-text">{{ progressText(row.status) }}</div>
            <el-progress :percentage="progressPercent(row.status)" :stroke-width="8" :show-text="false" />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="140" align="center">
          <template #default="{ row }"><el-tag size="small" :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="submittedAt" label="最近提交时间" width="170" show-overflow-tooltip />
        <el-table-column label="操作" width="470" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-row">
              <el-button size="small" @click="openDetail(row)">详情</el-button>
              <el-button size="small" type="primary" plain @click="openMembers(row)">成员</el-button>
              <el-button size="small" type="success" plain :disabled="!row.canEdit" @click="openEdit(row)">编辑</el-button>
              <el-button v-if="row.status === 0" size="small" type="danger" plain :disabled="!row.canEdit" @click="deleteProject(row)">删除</el-button>
              <el-button size="small" type="warning" plain :disabled="!row.canSubmit" @click="submitProject(row)">提交审批</el-button>
              <el-button size="small" type="info" plain :disabled="!row.canWithdraw" @click="withdrawProject(row)">撤回</el-button>
              <el-dropdown v-if="row.canUnitAudit || row.canFinanceAudit" @command="(cmd: string) => handleAuditCommand(row, cmd)">
                <el-button size="small" type="danger" plain>
                  审批
                  <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="pass">审批通过</el-dropdown-item>
                    <el-dropdown-item command="reject">驳回</el-dropdown-item>
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

    <el-dialog v-model="editDialog.visible" :title="editDialog.mode === 'create' ? '新增项目草稿' : '编辑项目'" width="980px" destroy-on-close>
      <el-form ref="editFormRef" :model="editDialog.form" :rules="editRules" label-width="100px">
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="项目编号" prop="projectCode">
              <el-input v-model="editDialog.form.projectCode" placeholder="如：PJT-2026-001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目类型" prop="projectType">
              <el-input v-model="editDialog.form.projectType" placeholder="纵向 / 横向 / 校级 / 教改等" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="editDialog.form.projectName" placeholder="请输入项目名称" maxlength="255" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker v-model="editDialog.form.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker v-model="editDialog.form.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预算总额" prop="totalBudget">
              <el-input-number v-model="editDialog.form.totalBudget" :min="0" :precision="2" :step="1000" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="项目说明" prop="description">
              <el-input v-model="editDialog.form.description" type="textarea" :rows="5" placeholder="填写研究内容、来源、计划、依据、预期成果等" />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="section-head">
          <div>
            <div class="section-title">预算分配</div>
            <div class="section-tip">按科研预算科目一次性分配金额。提交项目审批时，预算分配将一并校验且需与预算总额一致。</div>
          </div>
          <el-button plain @click="resetBudgetAmounts">清空分配金额</el-button>
        </div>

        <el-table :data="editDialog.form.budgetLines" border class="budget-table">
          <el-table-column label="预算科目编码" width="140">
            <template #default="{ row }">
              <el-tag effect="plain" type="info">{{ row.subjectCode }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="预算科目名称" min-width="260">
            <template #default="{ row }">
              <div class="subject-name-cell">{{ row.subjectName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="控制方式" width="120" align="center">
            <template #default="{ row }">
              <el-select v-model="row.controlMode" size="small" style="width:100%">
                <el-option label="禁止超支" value="禁止超支" />
                <el-option label="不控制" value="不控制" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="分配金额" width="180">
            <template #default="{ row }">
              <el-input-number v-model="row.approvedAmount" :min="0" :precision="2" :step="100" style="width:100%" />
            </template>
          </el-table-column>
          <el-table-column label="占比" width="120" align="right">
            <template #default="{ row }">
              {{ percentage(row.approvedAmount) }}
            </template>
          </el-table-column>
        </el-table>
        <div class="budget-summary">
          <span>预算总额：<b>{{ money(editDialog.form.totalBudget) }}</b></span>
          <span>分配合计：<b>{{ money(allocatedTotal) }}</b></span>
          <span :class="allocatedDiffClass">差额：<b>{{ money(allocatedDiff) }}</b></span>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="editDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="editDialog.saving" @click="submitEdit">保存草稿</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailDrawer.visible" title="项目详情 / 审批流" size="760px">
      <template v-if="detailDrawer.data">
        <div class="detail-grid">
          <div class="detail-item"><span>项目编号</span><b>{{ detailDrawer.data.projectCode }}</b></div>
          <div class="detail-item"><span>项目名称</span><b>{{ detailDrawer.data.projectName }}</b></div>
          <div class="detail-item"><span>项目类型</span><b>{{ detailDrawer.data.projectType || '-' }}</b></div>
          <div class="detail-item"><span>负责人</span><b>{{ detailDrawer.data.principalName || '-' }}</b></div>
          <div class="detail-item"><span>所属单位</span><b>{{ detailDrawer.data.unitName || '-' }}</b></div>
          <div class="detail-item"><span>当前状态</span><b>{{ statusText(detailDrawer.data.status) }}</b></div>
          <div class="detail-item"><span>项目周期</span><b>{{ detailDrawer.data.startDate || '-' }} ~ {{ detailDrawer.data.endDate || '-' }}</b></div>
          <div class="detail-item"><span>预算总额</span><b>{{ money(detailDrawer.data.totalBudget) }}</b></div>
          <div class="detail-item"><span>提交时间</span><b>{{ detailDrawer.data.submittedAt || '-' }}</b></div>
          <div class="detail-item"><span>创建时间</span><b>{{ detailDrawer.data.createdAt || '-' }}</b></div>
        </div>

        <el-card shadow="never" class="desc-card">
          <template #header><div class="desc-title">项目说明</div></template>
          <div class="desc-body">{{ detailDrawer.data.description || '暂无说明' }}</div>
        </el-card>

        <el-card shadow="never" class="desc-card">
          <template #header><div class="desc-title">预算分配</div></template>
          <el-table :data="detailDrawer.budgetLines" border>
            <el-table-column prop="subjectCode" label="科目编码" width="140" />
            <el-table-column prop="subjectName" label="科目名称" min-width="180" />
            <el-table-column label="分配金额" width="160" align="right">
              <template #default="{ row }">{{ money(row.approvedAmount) }}</template>
            </el-table-column>
            <el-table-column label="已用金额" width="160" align="right">
              <template #default="{ row }">{{ money(row.usedAmount) }}</template>
            </el-table-column>
            <el-table-column label="可用金额" width="160" align="right">
              <template #default="{ row }">{{ money(row.availableAmount) }}</template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card shadow="never" class="desc-card">
          <template #header><div class="desc-title">审批情况</div></template>
          <div class="audit-grid">
            <div class="audit-item"><span>二级单位审批人</span><b>{{ detailDrawer.data.unitAuditorName || '-' }}</b></div>
            <div class="audit-item"><span>二级单位审批时间</span><b>{{ detailDrawer.data.unitAuditAt || '-' }}</b></div>
            <div class="audit-item full"><span>二级单位意见</span><b>{{ detailDrawer.data.unitAuditComment || '-' }}</b></div>
            <div class="audit-item"><span>财务处审批人</span><b>{{ detailDrawer.data.financeAuditorName || '-' }}</b></div>
            <div class="audit-item"><span>财务处审批时间</span><b>{{ detailDrawer.data.financeAuditAt || '-' }}</b></div>
            <div class="audit-item full"><span>财务处意见</span><b>{{ detailDrawer.data.financeAuditComment || '-' }}</b></div>
          </div>
        </el-card>

        <el-card shadow="never" class="desc-card">
          <template #header><div class="desc-title">流程轨迹</div></template>
          <el-timeline>
            <el-timeline-item v-for="log in detailDrawer.data.auditLogs || []" :key="log.id" :timestamp="log.createdAt || '-'" placement="top">
              <div class="timeline-title">{{ log.action }}</div>
              <div class="timeline-text">操作人：{{ log.operatorName || '-' }}</div>
              <div class="timeline-text" v-if="log.comment">说明：{{ log.comment }}</div>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </template>
    </el-drawer>

    <el-dialog v-model="memberDialog.visible" title="项目成员管理" width="900px">
      <div class="member-top">
        <div>
          <div class="member-title">{{ memberDialog.projectName }}</div>
        </div>
        <div class="member-tools" v-if="memberDialog.canManage">
          <el-input v-model="memberDialog.addForm.username" placeholder="输入要添加的成员账号" clearable style="width: 220px" />
          <el-input v-model="memberDialog.addForm.memberRole" placeholder="成员职责，如：参与人" clearable style="width: 180px" />
          <el-button type="primary" :loading="memberDialog.adding" @click="submitAddMember">添加成员</el-button>
        </div>
      </div>

      <el-table :data="memberDialog.list" border v-loading="memberDialog.loading" table-layout="fixed">
        <el-table-column prop="username" label="账号" width="120" show-overflow-tooltip />
        <el-table-column prop="realName" label="姓名" width="100" show-overflow-tooltip />
        <el-table-column prop="unitName" label="所属单位" min-width="150" show-overflow-tooltip />
        <el-table-column prop="memberRole" label="项目角色" width="160" show-overflow-tooltip />
        <el-table-column prop="joinedAt" label="加入时间" width="170" show-overflow-tooltip />
        <el-table-column label="操作" width="100" align="center" v-if="memberDialog.canManage">
          <template #default="{ row }">
            <el-button size="small" type="danger" plain @click="removeMember(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ArrowDown } from '@element-plus/icons-vue';
import { computed, onMounted, reactive, ref } from 'vue';
import { storeToRefs } from 'pinia';
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus';
import { useUserStore } from '@/stores/user';
import {
  apiProjectAddMember,
  apiProjectAudit,
  apiProjectCreate,
  apiProjectDelete,
  apiProjectDetail,
  apiProjectMembers,
  apiProjectPage,
  apiProjectRemoveMember,
  apiProjectSubmit,
  apiProjectUpdate,
  apiProjectWithdraw,
  type ProjectBudgetLineReq,
  type ProjectDetailVO,
  type ProjectMemberVO,
  type ProjectVO,
} from '@/api/project';
import { apiBudgetSubjectTree, apiProjectBudgetList, type BudgetSubjectNode, type ProjectBudgetVO } from '@/api/budget';

const userStore = useUserStore();
const { roles } = storeToRefs(userStore);
const editFormRef = ref<FormInstance>();

type ScopeKey = 'all' | 'lead' | 'joined' | 'todo';
type ControlMode = '禁止超支' | '不控制';

const loading = ref(false);
const tableData = ref<ProjectVO[]>([]);
const pager = reactive({ pageNo: 1, pageSize: 10, total: 0 });
const activeScope = ref<ScopeKey>('all');
const searchForm = reactive<{ keyword: string; status: number | null; todoOnly: boolean }>({ keyword: '', status: null, todoOnly: false });
type SubjectOption = { id: number; code: string; name: string };
type EditableBudgetLine = ProjectBudgetLineReq & { subjectCode: string; subjectName: string; controlMode: ControlMode };
const subjectOptions = ref<SubjectOption[]>([]);

const editDialog = reactive({
  visible: false,
  mode: 'create' as 'create' | 'edit',
  saving: false,
  form: {
    id: undefined as number | undefined,
    projectCode: '',
    projectName: '',
    projectType: '',
    startDate: '',
    endDate: '',
    totalBudget: undefined as number | undefined,
    description: '',
    budgetLines: [] as EditableBudgetLine[],
  },
});

const detailDrawer = reactive({
  visible: false,
  data: null as ProjectDetailVO | null,
  budgetLines: [] as ProjectBudgetVO[],
});

const memberDialog = reactive({
  visible: false,
  projectId: 0,
  projectName: '',
  canManage: false,
  loading: false,
  adding: false,
  list: [] as ProjectMemberVO[],
  addForm: {
    username: '',
    memberRole: '项目成员',
  },
});

const editRules: FormRules = {
  projectCode: [{ required: true, message: '请输入项目编号', trigger: 'blur' }],
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  projectType: [{ required: true, message: '请输入项目类型', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  totalBudget: [{ required: true, message: '请填写预算总额', trigger: 'change' }],
};

const canCreate = computed(() => roles.value.includes('PI') || roles.value.includes('ADMIN'));
const canAuditAny = computed(() => roles.value.includes('ADMIN') || roles.value.includes('UNIT_ADMIN') || roles.value.includes('FINANCE'));
const showTeacherTabs = computed(() => roles.value.includes('PI') || roles.value.includes('ADMIN'));
const scopeTabs = computed(() => {
  const tabs: Array<{ key: ScopeKey; label: string }> = [{ key: 'all', label: '全部项目' }];
  if (showTeacherTabs.value) tabs.push({ key: 'lead', label: '我负责的' }, { key: 'joined', label: '我参与的' });
  if (canAuditAny.value) tabs.push({ key: 'todo', label: '待我审批' });
  return tabs;
});
const allocatedTotal = computed(() => editDialog.form.budgetLines.reduce((sum, item) => sum + Number(item.approvedAmount || 0), 0));
const allocatedDiff = computed(() => Number(editDialog.form.totalBudget || 0) - allocatedTotal.value);
const allocatedDiffClass = computed(() => Math.abs(allocatedDiff.value) < 0.005 ? 'budget-ok' : 'budget-warn');

function flattenSubjects(list: BudgetSubjectNode[], out: SubjectOption[]) {
  for (const item of list || []) {
    const enabledChildren = (item.children || []).filter((child) => child.enabled !== 0);
    const isLeaf = enabledChildren.length === 0;
    const isRoot = item.code === 'ROOT';
    if (item.enabled !== 0 && isLeaf && !isRoot) out.push({ id: item.id, code: item.code, name: item.name });
    if (item.children?.length) flattenSubjects(item.children, out);
  }
}

async function loadSubjectOptions() {
  const tree = await apiBudgetSubjectTree();
  const out: SubjectOption[] = [];
  flattenSubjects(tree || [], out);
  const seen = new Set<number>();
  subjectOptions.value = out.filter((item) => !seen.has(item.id) && seen.add(item.id));
}

function statusText(status?: number) {
  switch (status) {
    case 0: return '草稿';
    case 1: return '待二级单位审批';
    case 2: return '待财务处审批';
    case 3: return '立项通过';
    case 4: return '已驳回';
    case 5: return '已结题';
    case 6: return '停用';
    default: return '-';
  }
}
function statusType(status?: number) {
  switch (status) {
    case 0: return 'info';
    case 1:
    case 2: return 'warning';
    case 3: return 'success';
    case 4:
    case 6: return 'danger';
    case 5: return 'primary';
    default: return 'info';
  }
}
function progressText(status?: number) {
  switch (status) {
    case 0: return '草稿待完善';
    case 1: return '二级单位审批中';
    case 2: return '财务处审批中';
    case 3: return '已完成立项';
    case 4: return '已驳回待修改';
    case 5: return '项目已结题';
    case 6: return '项目已停用';
    default: return '-';
  }
}
function progressPercent(status?: number) {
  switch (status) {
    case 0: return 18;
    case 1: return 48;
    case 2: return 72;
    case 3: return 100;
    case 4: return 30;
    case 5:
    case 6: return 100;
    default: return 0;
  }
}
function money(value?: number | null) {
  if (value === null || value === undefined || value === ('' as never)) return '-';
  return `¥ ${Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}
function percentage(value?: number | null) {
  const total = Number(editDialog.form.totalBudget || 0);
  const amount = Number(value || 0);
  if (total <= 0 || amount <= 0) return '-';
  return `${((amount / total) * 100).toFixed(2)}%`;
}
function defaultControlMode(value?: number | null): ControlMode {
  return Number(value || 0) > 0 ? '禁止超支' : '不控制';
}

function changeScope(scope: ScopeKey) {
  activeScope.value = scope;
  searchForm.todoOnly = scope === 'todo';
  pager.pageNo = 1;
  loadPage();
}
function doSearch() { pager.pageNo = 1; loadPage(); }
function resetSearch() { searchForm.keyword = ''; searchForm.status = null; searchForm.todoOnly = activeScope.value === 'todo'; pager.pageNo = 1; loadPage(); }
function onPageChange(pageNo: number) { pager.pageNo = pageNo; loadPage(); }
function onSizeChange(size: number) { pager.pageSize = size; pager.pageNo = 1; loadPage(); }

async function loadPage() {
  loading.value = true;
  try {
    const viewType = activeScope.value === 'lead' || activeScope.value === 'joined' ? activeScope.value : 'all';
    const res = await apiProjectPage({ pageNo: pager.pageNo, pageSize: pager.pageSize, keyword: searchForm.keyword || undefined, status: searchForm.status, todoOnly: canAuditAny.value ? searchForm.todoOnly : false, viewType });
    tableData.value = res.records || [];
    pager.total = Number(res.total || 0);
  } finally {
    loading.value = false;
  }
}

function buildBudgetLines(existing: ProjectBudgetVO[] = []): EditableBudgetLine[] {
  const map = new Map<number, number>();
  for (const line of existing) {
    if (line.subjectId) map.set(line.subjectId, Number(line.approvedAmount || 0));
  }
  return subjectOptions.value.map((item) => ({
    subjectId: item.id,
    subjectCode: item.code,
    subjectName: item.name,
    approvedAmount: map.get(item.id) ?? null,
    controlMode: defaultControlMode(map.get(item.id)),
  }));
}
function resetEditForm() {
  editDialog.form.id = undefined;
  editDialog.form.projectCode = '';
  editDialog.form.projectName = '';
  editDialog.form.projectType = '';
  editDialog.form.startDate = '';
  editDialog.form.endDate = '';
  editDialog.form.totalBudget = undefined;
  editDialog.form.description = '';
  editDialog.form.budgetLines = buildBudgetLines();
}
function resetBudgetAmounts() {
  editDialog.form.budgetLines.forEach((line) => { line.approvedAmount = null; });
}

function openCreate() {
  editDialog.mode = 'create';
  resetEditForm();
  editDialog.visible = true;
}

async function openEdit(row: ProjectVO) {
  if (!row.canEdit) return;
  editDialog.mode = 'edit';
  resetEditForm();
  editDialog.visible = true;
  const [detail, budgetLines] = await Promise.all([apiProjectDetail(row.id), apiProjectBudgetList(row.id)]);
  editDialog.form.id = row.id;
  editDialog.form.projectCode = detail.projectCode || '';
  editDialog.form.projectName = detail.projectName || '';
  editDialog.form.projectType = detail.projectType || '';
  editDialog.form.startDate = detail.startDate || '';
  editDialog.form.endDate = detail.endDate || '';
  editDialog.form.totalBudget = detail.totalBudget;
  editDialog.form.description = detail.description || '';
  editDialog.form.budgetLines = buildBudgetLines(budgetLines || []);
}

async function submitEdit() {
  await editFormRef.value?.validate();
  if (editDialog.form.startDate && editDialog.form.endDate && editDialog.form.startDate > editDialog.form.endDate) {
    ElMessage.warning('结束日期不能早于开始日期');
    return;
  }
  if (Math.abs(allocatedDiff.value) >= 0.005) {
    ElMessage.warning(`预算分配未完成：分配合计必须等于预算总额（当前差额 ${money(allocatedDiff.value)}）`);
    return;
  }
  const budgetLines = editDialog.form.budgetLines
    .filter((item) => item.subjectId && Number(item.approvedAmount || 0) > 0)
    .map((item) => ({ subjectId: item.subjectId, approvedAmount: item.approvedAmount }));
  editDialog.saving = true;
  try {
    const payload = {
      projectCode: editDialog.form.projectCode,
      projectName: editDialog.form.projectName,
      projectType: editDialog.form.projectType,
      startDate: editDialog.form.startDate,
      endDate: editDialog.form.endDate,
      totalBudget: editDialog.form.totalBudget,
      description: editDialog.form.description,
      budgetLines,
    };
    if (editDialog.mode === 'create') {
      await apiProjectCreate(payload);
      ElMessage.success('项目草稿已创建');
    } else if (editDialog.form.id) {
      await apiProjectUpdate({ id: editDialog.form.id, ...payload });
      ElMessage.success('项目已更新');
    }
    editDialog.visible = false;
    loadPage();
  } finally {
    editDialog.saving = false;
  }
}

async function openDetail(row: ProjectVO) {
  detailDrawer.visible = true;
  const [detail, budgetLines] = await Promise.all([apiProjectDetail(row.id), apiProjectBudgetList(row.id)]);
  detailDrawer.data = detail;
  detailDrawer.budgetLines = budgetLines || [];
}

async function openMembers(row: ProjectVO) {
  memberDialog.visible = true;
  memberDialog.projectId = row.id;
  memberDialog.projectName = row.projectName;
  memberDialog.canManage = !!row.canManageMembers;
  memberDialog.addForm.username = '';
  memberDialog.addForm.memberRole = '项目成员';
  await loadMembers();
}

async function loadMembers() {
  memberDialog.loading = true;
  try { memberDialog.list = await apiProjectMembers(memberDialog.projectId); }
  finally { memberDialog.loading = false; }
}

async function submitAddMember() {
  if (!memberDialog.addForm.username.trim()) {
    ElMessage.warning('请输入成员账号');
    return;
  }
  memberDialog.adding = true;
  try {
    await apiProjectAddMember(memberDialog.projectId, memberDialog.addForm.username.trim(), memberDialog.addForm.memberRole.trim() || '项目成员');
    ElMessage.success('成员添加成功');
    memberDialog.addForm.username = '';
    memberDialog.addForm.memberRole = '项目成员';
    await loadMembers();
    await loadPage();
  } finally {
    memberDialog.adding = false;
  }
}

async function removeMember(row: ProjectMemberVO) {
  await ElMessageBox.confirm(`确认将成员【${row.realName}】移出项目吗？`, '提示', { type: 'warning' });
  await apiProjectRemoveMember(memberDialog.projectId, row.userId);
  ElMessage.success('成员已移除');
  await loadMembers();
  await loadPage();
}
async function submitProject(row: ProjectVO) {
  if (!row.canSubmit) return;
  await ElMessageBox.confirm('提交后将同步提交预算分配并进入立项审批流，审批期间不可继续编辑。确认提交吗？', '提交审批', { type: 'warning' });
  await apiProjectSubmit(row.id);
  ElMessage.success('已提交立项审批');
  await loadPage();
}
async function withdrawProject(row: ProjectVO) {
  if (!row.canWithdraw) return;
  await ElMessageBox.confirm('撤回后项目会回到草稿状态，需重新提交审批。确认撤回吗？', '撤回审批', { type: 'warning' });
  await apiProjectWithdraw(row.id);
  ElMessage.success('项目已撤回到草稿');
  await loadPage();
}
async function deleteProject(row: ProjectVO) {
  if (!row.canEdit || row.status !== 0) return;
  await ElMessageBox.confirm(`确认删除项目草稿【${row.projectName}】吗？删除后不可恢复。`, '删除项目', { type: 'warning' });
  await apiProjectDelete(row.id);
  ElMessage.success('项目草稿已删除');
  await loadPage();
}
function handleAuditCommand(row: ProjectVO, command: string) { if (command === 'pass') passAudit(row); else if (command === 'reject') rejectAudit(row); }
async function passAudit(row: ProjectVO) {
  const stage = row.canUnitAudit ? '二级单位审批' : '财务处审批';
  await ElMessageBox.confirm(`确认通过【${row.projectName}】的${stage}吗？`, '审批通过', { type: 'success' });
  await apiProjectAudit(row.id, 'pass');
  ElMessage.success('审批已通过');
  await loadPage();
}
async function rejectAudit(row: ProjectVO) {
  const result = await ElMessageBox.prompt('请填写驳回原因。', '驳回项目', { inputType: 'textarea', inputValidator: (value) => !!value.trim() || '请填写驳回意见' });
  await apiProjectAudit(row.id, 'reject', result.value);
  ElMessage.success('已驳回项目');
  await loadPage();
}

onMounted(async () => {
  if (roles.value.includes('PI')) activeScope.value = 'lead';
  if (!roles.value.includes('PI') && canAuditAny.value) { activeScope.value = 'todo'; searchForm.todoOnly = true; }
  await loadSubjectOptions();
  await loadPage();
});
</script>

<style scoped>
.segment-card, .search-card, .table-card { border-radius: 16px; border: none; }
.segment-card, .search-card, .table-card { margin-top: 16px; }
.segment-wrap { display: flex; flex-direction: column; gap: 14px; }
.segment-title { font-size: 16px; font-weight: 700; color: #0f172a; }
.segment-list { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 12px; }
.segment-item { border: 1px solid #dbeafe; background: linear-gradient(180deg, #f8fbff 0%, #f1f5ff 100%); border-radius: 14px; padding: 14px 16px; cursor: pointer; transition: all 0.2s ease; }
.segment-item:hover, .segment-item.active { border-color: #2563eb; box-shadow: 0 10px 22px rgba(37, 99, 235, 0.12); transform: translateY(-1px); }
.segment-name { font-weight: 700; color: #0f172a; }
.card-head { display: flex; align-items: center; justify-content: space-between; gap: 12px; font-weight: 700; }
.simple-head { justify-content: space-between; }
.head-right { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; justify-content: flex-end; }
.search-form { display: flex; flex-wrap: wrap; }
.project-table :deep(.el-table__cell) { height: 62px; }
.project-name-cell { display: flex; flex-direction: column; gap: 8px; }
.project-main-line { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; }
.project-main-line b { color: #0f172a; }
.project-sub-line { display: flex; flex-wrap: wrap; gap: 14px; color: #64748b; font-size: 13px; }
.step-text { margin-bottom: 6px; color: #475569; font-size: 13px; }
.action-row { display: flex; flex-wrap: wrap; justify-content: center; gap: 8px; }
.pager-wrap { margin-top: 16px; display: flex; justify-content: flex-end; }
.section-head { display:flex; align-items:center; justify-content:space-between; gap:16px; margin: 10px 0 12px; }
.section-title { font-size: 16px; font-weight: 700; color: #0f172a; }
.section-tip { margin-top: 4px; color:#64748b; font-size:13px; }
.subject-name-cell { color: #0f172a; font-weight: 500; }
.budget-summary { display:flex; justify-content:flex-end; gap:22px; margin-top:12px; color:#475569; flex-wrap:wrap; }
.budget-summary b { color:#0f172a; }
.budget-ok { color:#16a34a; }
.budget-warn { color:#dc2626; }
.detail-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; }
.detail-item, .audit-item { border-radius: 14px; background: #f8fafc; border: 1px solid #e2e8f0; padding: 14px 16px; display: flex; flex-direction: column; gap: 8px; }
.detail-item span, .audit-item span { color: #64748b; font-size: 13px; }
.detail-item b, .audit-item b { color: #0f172a; line-height: 1.8; word-break: break-word; }
.desc-card { margin-top: 16px; border-radius: 16px; }
.desc-title { font-weight: 700; }
.desc-body { white-space: pre-wrap; line-height: 1.8; color: #334155; }
.audit-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; }
.audit-item.full { grid-column: 1 / -1; }
.timeline-title { font-weight: 700; color: #0f172a; }
.timeline-text { margin-top: 6px; color: #475569; line-height: 1.8; }
.member-top { display: flex; justify-content: space-between; gap: 16px; align-items: center; margin-bottom: 14px; }
.member-title { font-size:18px; font-weight:700; color:#0f172a; }
.member-tools { display:flex; gap:10px; flex-wrap:wrap; }
@media (max-width: 960px) {
  .detail-grid, .audit-grid { grid-template-columns: 1fr; }
  .member-top, .section-head { flex-direction:column; align-items:flex-start; }
}
</style>
