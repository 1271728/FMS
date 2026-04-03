import { defineStore } from "pinia";
import { apiLogin, apiMe, type MeResp } from "@/api/auth";
import { clearToken, getToken, setToken } from "@/api/http";

const ROLE_ALIASES: Record<string, string[]> = {
  ADMIN: ["ADMIN"],
  PI: ["PI"],
  UNIT_ADMIN: ["UNIT_ADMIN"],
  FINANCE: ["FINANCE"],
};

function normalizeRoles(input?: string[] | null): string[] {
  const source = Array.isArray(input) ? input.filter(Boolean).map((item) => String(item).trim().toUpperCase()) : [];
  const normalized: string[] = [];
  for (const [canonical, aliases] of Object.entries(ROLE_ALIASES)) {
    if (source.some((role) => aliases.includes(role))) normalized.push(canonical);
  }
  return Array.from(new Set(normalized));
}

function normalizeMe(raw?: Partial<MeResp> | null): MeResp | null {
  if (!raw) return null;
  return {
    id: Number(raw.id || 0),
    username: raw.username || "",
    realName: raw.realName || "",
    unitId: Number(raw.unitId || 0),
    unitName: raw.unitName || "",
    roles: normalizeRoles(raw.roles),
    perms: Array.isArray(raw.perms) ? raw.perms : [],
    phone: raw.phone || "",
    email: raw.email || "",
    status: raw.status == null ? 1 : Number(raw.status),
    lastLoginAt: raw.lastLoginAt || "",
  };
}

export const useUserStore = defineStore("user", {
  state: () => ({
    token: getToken() || "",
    me: null as MeResp | null,
    loaded: false,
  }),
  getters: {
    roles: (s) => s.me?.roles || [],
    isAdmin: (s) => (s.me?.roles || []).includes("ADMIN"),
    isPi: (s) => (s.me?.roles || []).includes("PI"),
    isUnitAdmin: (s) => (s.me?.roles || []).includes("UNIT_ADMIN"),
    isFinance: (s) => (s.me?.roles || []).includes("FINANCE"),
    canViewProjectModule: (s) => {
      const roles = s.me?.roles || [];
      return roles.includes("PI") || roles.includes("ADMIN") || roles.includes("UNIT_ADMIN") || roles.includes("FINANCE");
    },
    canViewBudgetOverview: (s) => {
      const roles = s.me?.roles || [];
      return roles.includes("PI") || roles.includes("ADMIN") || roles.includes("UNIT_ADMIN") || roles.includes("FINANCE");
    },
    canManageBudgetAdjust: (s) => {
      const roles = s.me?.roles || [];
      return roles.includes("PI") || roles.includes("ADMIN");
    },
    canManageReimburse: (s) => {
      const roles = s.me?.roles || [];
      return roles.includes("PI") || roles.includes("ADMIN") || roles.includes("UNIT_ADMIN") || roles.includes("FINANCE");
    },
    canUseWorkflowCenter: (s) => {
      const roles = s.me?.roles || [];
      return roles.includes("ADMIN") || roles.includes("UNIT_ADMIN") || roles.includes("FINANCE");
    },
    defaultHomePath: () => "/home",
    primaryRoleLabel: (s) => {
      const roles = s.me?.roles || [];
      if (roles.includes("ADMIN")) return "管理员端";
      if (roles.includes("FINANCE")) return "财务处端";
      if (roles.includes("UNIT_ADMIN")) return "二级单位审核端";
      if (roles.includes("PI")) return "项目负责人端";
      return "默认工作台";
    },
    displayName: (s) => s.me?.realName || s.me?.username || "未登录",
  },
  actions: {
    async login(username: string, password: string) {
      const res = await apiLogin({ username, password });
      this.token = res.tokenValue;
      setToken(res.tokenValue);
      this.me = normalizeMe(res.user as any);
      this.loaded = !!this.me;
      try {
        await this.fetchMe();
      } catch {
        // 保留登录接口返回的角色信息，避免 /me 字段不一致时无法跳转
      }
    },
    async fetchMe() {
      this.me = normalizeMe(await apiMe());
      this.loaded = true;
    },
    logoutLocal() {
      this.token = "";
      this.me = null;
      this.loaded = false;
      clearToken();
    },
    hasRole(role: string) {
      return (this.me?.roles || []).includes(role);
    },
    hasAnyRole(roles: string[]) {
      const mine = this.me?.roles || [];
      return roles.some((role) => mine.includes(role));
    },
  },
});
