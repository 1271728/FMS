import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";
import type { AccessKey } from "@/constants/access";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", component: () => import("@/views/LoginView.vue"), meta: { public: true } },
    { path: "/login", redirect: "/" },
    {
      path: "/app",
      component: () => import("@/layouts/MainLayout.vue"),
      children: [
        { path: "/home", component: () => import("@/views/home/DashboardHomeView.vue"), meta: { access: "HOME" as AccessKey } },
        { path: "/admin/users", component: () => import("@/views/admin/AdminUserManageView.vue"), meta: { access: "ADMIN_USERS" as AccessKey } },
        { path: "/project/manage", component: () => import("@/views/project/ProjectManageView.vue"), meta: { access: "PROJECT_MANAGE" as AccessKey } },
        { path: "/budget/overview", component: () => import("@/views/budget/BudgetOverviewView.vue"), meta: { access: "BUDGET_OVERVIEW" as AccessKey } },
        { path: "/budget/adjust", component: () => import("@/views/budget/BudgetAdjustManageView.vue"), meta: { access: "BUDGET_ADJUST" as AccessKey } },
        { path: "/reimburse/manage", component: () => import("@/views/reimburse/ReimburseManageView.vue"), meta: { access: "REIMBURSE_MANAGE" as AccessKey } },
        { path: "/workflow/center", component: () => import("@/views/workflow/WorkflowCenterView.vue"), meta: { access: "WORKFLOW_CENTER" as AccessKey } },
        { path: "/msg/center", component: () => import("@/views/msg/MsgCenterView.vue"), meta: { access: "MSG_CENTER" as AccessKey } },
      ],
    },
  ],
});

router.beforeEach(async (to) => {
  const user = useUserStore();

  if (to.meta.public) {
    if (!user.token) return true;
    if (!user.loaded) {
      try {
        await user.fetchMe();
      } catch {
        user.logoutLocal();
        return true;
      }
    }
    return { path: user.defaultHomePath };
  }

  if (!user.token) {
    return { path: "/", query: { redirect: to.fullPath } };
  }

  if (!user.loaded) {
    try {
      await user.fetchMe();
    } catch {
      user.logoutLocal();
      return { path: "/" };
    }
  }

  if (to.path === "/app") {
    return { path: user.defaultHomePath };
  }

  const access = to.meta.access as AccessKey | undefined;
  if (access && !user.canAccess(access)) {
    return { path: user.defaultHomePath };
  }

  return true;
});

export default router;
