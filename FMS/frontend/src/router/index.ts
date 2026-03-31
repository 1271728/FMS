import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", component: () => import("@/views/LoginView.vue"), meta: { public: true } },
    { path: "/login", redirect: "/" },
    {
      path: "/app",
      component: () => import("@/layouts/MainLayout.vue"),
      children: [
        { path: "/home", component: () => import("@/views/home/DashboardHomeView.vue"), meta: { roles: ["PI", "ADMIN", "UNIT_ADMIN", "FINANCE"] } },
        { path: "/admin/users", component: () => import("@/views/admin/AdminUserManageView.vue"), meta: { roles: ["ADMIN"] } },
        { path: "/project/manage", component: () => import("@/views/project/ProjectManageView.vue"), meta: { roles: ["PI", "ADMIN", "UNIT_ADMIN", "FINANCE"] } },
        { path: "/budget/overview", component: () => import("@/views/budget/BudgetOverviewView.vue"), meta: { roles: ["PI", "ADMIN", "UNIT_ADMIN", "FINANCE"] } },
        { path: "/budget/adjust", component: () => import("@/views/budget/BudgetAdjustManageView.vue"), meta: { roles: ["PI", "ADMIN"] } },
        { path: "/reimburse/manage", component: () => import("@/views/reimburse/ReimburseManageView.vue"), meta: { roles: ["PI", "ADMIN", "UNIT_ADMIN", "FINANCE"] } },
        { path: "/workflow/center", component: () => import("@/views/workflow/WorkflowCenterView.vue"), meta: { roles: ["ADMIN", "UNIT_ADMIN", "FINANCE"] } },
        { path: "/msg/center", component: () => import("@/views/msg/MsgCenterView.vue"), meta: { roles: ["PI", "ADMIN", "UNIT_ADMIN", "FINANCE"] } },
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

  const roles = to.meta.roles as string[] | undefined;
  if (roles && !user.hasAnyRole(roles)) {
    return { path: user.defaultHomePath };
  }

  return true;
});

export default router;
