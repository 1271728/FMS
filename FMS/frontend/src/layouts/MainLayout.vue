<template>
  <div class="layout-shell">
    <aside class="sidebar">
      <div class="brand-box" @click="goHome">
        <div class="brand-mark">RF</div>
        <div>
          <div class="brand-title">科研经费报销管理系统</div>
        </div>
      </div>

      <el-scrollbar class="menu-scroll">
        <el-menu :default-active="route.path" router class="side-menu">
          <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
            <span>{{ item.label }}</span>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
    </aside>

    <div class="main-wrap">
      <header class="topbar">
        <div class="page-title">{{ currentTitle }}</div>
        <div class="topbar-right">
          <el-button v-if="route.path !== '/home'" text @click="goHome">首页</el-button>
          <button class="user-trigger" @click="drawerVisible = true">{{ user.displayName }}</button>
        </div>
      </header>

      <main class="page-body">
        <router-view />
      </main>
    </div>

    <el-drawer v-model="drawerVisible" title="个人信息" size="420px" destroy-on-close>
      <div class="profile-panel">
        <div class="profile-name">{{ user.displayName }}</div>
        <div class="profile-role">{{ (user.me?.roles || []).join(' / ') || '未分配角色' }}</div>

        <el-descriptions :column="1" border class="profile-desc">
          <el-descriptions-item label="用户名">{{ user.me?.username || '-' }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ user.me?.realName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="单位">{{ user.me?.unitName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ user.me?.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ user.me?.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="最近登录">{{ user.me?.lastLoginAt || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="section-title">修改密码</div>
        <el-form ref="pwdRef" :model="pwdForm" :rules="pwdRules" label-position="top">
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入当前密码" />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
          </el-form-item>
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
          </el-form-item>
          <el-button type="primary" :loading="pwdLoading" @click="submitPassword">确认修改</el-button>
        </el-form>

        <div class="drawer-footer">
          <el-button @click="refreshProfile">刷新信息</el-button>
          <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { apiChangePassword, apiLogout } from "@/api/auth";
import { useUserStore } from "@/stores/user";

const route = useRoute();
const router = useRouter();
const user = useUserStore();
const drawerVisible = ref(false);
const pwdRef = ref<FormInstance>();
const pwdLoading = ref(false);

const pwdForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: "请输入旧密码", trigger: "blur" }],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, message: "新密码至少 6 位", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请再次输入新密码", trigger: "blur" },
    {
      validator: (_rule, value, callback) => {
        if (!value) callback(new Error("请再次输入新密码"));
        else if (value !== pwdForm.newPassword) callback(new Error("两次输入的新密码不一致"));
        else callback();
      },
      trigger: "blur",
    },
  ],
};

const metaMap: Record<string, string> = {
  "/home": "首页",
  "/admin/users": "用户与权限管理",
  "/project/manage": "项目管理",
  "/budget/overview": "预算总览",
  "/budget/adjust": "预算调整单",
  "/reimburse/manage": "报销单管理",
  "/workflow/center": "审批中心",
  "/msg/center": "消息中心",
};

const currentTitle = computed(() => metaMap[route.path] || "科研经费报销管理系统");

const menuItems = computed(() => {
  const items: Array<{ path: string; label: string }> = [{ path: "/home", label: "首页" }];
  if (user.isAdmin) items.push({ path: "/admin/users", label: "用户与权限" });
  if (user.canViewProjectModule) items.push({ path: "/project/manage", label: "项目管理" });
  if (user.canViewBudgetOverview) items.push({ path: "/budget/overview", label: "预算总览" });
  if (user.canManageBudgetAdjust) items.push({ path: "/budget/adjust", label: "预算调整单" });
  if (user.canManageReimburse) items.push({ path: "/reimburse/manage", label: "报销单管理" });
  if (user.canUseWorkflowCenter) items.push({ path: "/workflow/center", label: "审批中心" });
  items.push({ path: "/msg/center", label: "消息中心" });
  return items;
});

function goHome() {
  router.push("/home");
}

async function refreshProfile() {
  await user.fetchMe();
  ElMessage.success("个人信息已刷新");
}

async function submitPassword() {
  await pwdRef.value?.validate();
  pwdLoading.value = true;
  try {
    await apiChangePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword,
    });
    ElMessage.success("密码修改成功，请使用新密码重新登录");
    pwdForm.oldPassword = "";
    pwdForm.newPassword = "";
    pwdForm.confirmPassword = "";
    drawerVisible.value = false;
    await handleLogout();
  } finally {
    pwdLoading.value = false;
  }
}

async function handleLogout() {
  try {
    await apiLogout();
  } catch {
    // ignore network/logout state errors and clear locally anyway
  }
  user.logoutLocal();
  router.replace("/");
}
</script>

<style scoped>
.layout-shell {
  min-height: 100vh;
  background: #f5f6f8;
  display: grid;
  grid-template-columns: 240px minmax(0, 1fr);
}
.sidebar {
  background: #ffffff;
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
.brand-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 18px;
  border-bottom: 1px solid #eef1f4;
  cursor: pointer;
}
.brand-mark {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  background: #111827;
  color: #fff;
  font-weight: 700;
}
.brand-title {
  font-size: 15px;
  font-weight: 700;
  color: #111827;
}
.brand-subtitle {
  margin-top: 4px;
  font-size: 12px;
  color: #6b7280;
}
.menu-scroll {
  flex: 1;
}
.side-menu {
  border-right: none;
  padding: 10px 10px 24px;
}
.side-menu :deep(.el-menu-item) {
  height: 42px;
  line-height: 42px;
  border-radius: 10px;
  margin-bottom: 6px;
}
.side-menu :deep(.el-menu-item.is-active) {
  background: #111827;
  color: #ffffff;
}
.main-wrap {
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.topbar {
  height: 68px;
  padding: 0 24px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
}
.topbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
}
.user-trigger {
  border: none;
  background: transparent;
  color: #111827;
  font-size: 14px;
  font-weight: 600;
  padding: 8px 0;
}
.page-body {
  min-width: 0;
  padding: 20px 24px 24px;
}
.profile-panel {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.profile-name {
  font-size: 22px;
  font-weight: 700;
  color: #111827;
}
.profile-role {
  margin-top: -10px;
  color: #6b7280;
}
.profile-desc {
  margin-top: 4px;
}
.section-title {
  padding-top: 8px;
  font-size: 16px;
  font-weight: 700;
  color: #111827;
}
.drawer-footer {
  display: flex;
  gap: 12px;
  padding-top: 8px;
}
@media (max-width: 960px) {
  .layout-shell {
    grid-template-columns: 1fr;
  }
  .sidebar {
    min-height: auto;
    border-right: none;
    border-bottom: 1px solid #e5e7eb;
  }
  .topbar {
    padding: 12px 16px;
    height: auto;
    align-items: flex-start;
    flex-direction: column;
  }
  .topbar-right {
    width: 100%;
    justify-content: space-between;
  }
  .page-body {
    padding: 16px;
  }
}
</style>
