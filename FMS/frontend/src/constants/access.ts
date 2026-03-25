export type RoleCode = "ADMIN" | "PI" | "UNIT_ADMIN" | "FINANCE";

export type AccessKey =
  | "HOME"
  | "ADMIN_USERS"
  | "PROJECT_MANAGE"
  | "BUDGET_OVERVIEW"
  | "BUDGET_ADJUST"
  | "REIMBURSE_MANAGE"
  | "WORKFLOW_CENTER"
  | "MSG_CENTER";

export const ACCESS_RULES: Record<AccessKey, RoleCode[]> = {
  HOME: ["PI", "ADMIN", "UNIT_ADMIN", "FINANCE"],
  ADMIN_USERS: ["ADMIN"],
  PROJECT_MANAGE: ["PI", "ADMIN", "UNIT_ADMIN", "FINANCE"],
  BUDGET_OVERVIEW: ["PI", "ADMIN", "UNIT_ADMIN", "FINANCE"],
  BUDGET_ADJUST: ["PI", "ADMIN", "UNIT_ADMIN", "FINANCE"],
  REIMBURSE_MANAGE: ["PI", "ADMIN", "UNIT_ADMIN", "FINANCE"],
  WORKFLOW_CENTER: ["ADMIN", "UNIT_ADMIN", "FINANCE"],
  MSG_CENTER: ["PI", "ADMIN", "UNIT_ADMIN", "FINANCE"],
};

export const NAV_ITEMS: Array<{ path: string; label: string; access: AccessKey }> = [
  { path: "/home", label: "首页", access: "HOME" },
  { path: "/admin/users", label: "用户与权限", access: "ADMIN_USERS" },
  { path: "/project/manage", label: "项目管理", access: "PROJECT_MANAGE" },
  { path: "/budget/overview", label: "预算总览", access: "BUDGET_OVERVIEW" },
  { path: "/budget/adjust", label: "预算调整单", access: "BUDGET_ADJUST" },
  { path: "/reimburse/manage", label: "报销单管理", access: "REIMBURSE_MANAGE" },
  { path: "/workflow/center", label: "审批中心", access: "WORKFLOW_CENTER" },
  { path: "/msg/center", label: "消息中心", access: "MSG_CENTER" },
];

export const TITLE_MAP: Record<string, string> = {
  "/home": "首页",
  "/admin/users": "用户与权限管理",
  "/project/manage": "项目管理",
  "/budget/overview": "预算总览",
  "/budget/adjust": "预算调整单",
  "/reimburse/manage": "报销单管理",
  "/workflow/center": "审批中心",
  "/msg/center": "消息中心",
};

export function normalizeRoleCode(role: string): RoleCode | null {
  const normalized = String(role || "").trim().toUpperCase();
  if (normalized === "ADMIN" || normalized === "PI" || normalized === "UNIT_ADMIN" || normalized === "FINANCE") {
    return normalized;
  }
  return null;
}

export function hasAccess(roles: string[], access: AccessKey): boolean {
  const allowed = ACCESS_RULES[access] || [];
  if (!allowed.length) return false;
  const mine = new Set(roles.map((role) => normalizeRoleCode(role)).filter(Boolean));
  return allowed.some((role) => mine.has(role));
}
