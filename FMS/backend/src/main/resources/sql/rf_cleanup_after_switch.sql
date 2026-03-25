-- 在后端代码已经切换到 rf_* 主表体系，并完成联调验证后再执行
-- 建议先完整备份数据库

RENAME TABLE
  project TO z_old_project,
  project_member TO z_old_project_member,
  project_audit_log TO z_old_project_audit_log,
  budget_subject TO z_old_budget_subject,
  project_budget TO z_old_project_budget,
  reimburse TO z_old_reimburse,
  reimburse_item TO z_old_reimburse_item,
  reimburse_audit_log TO z_old_reimburse_audit_log;
