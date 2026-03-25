-- 项目模块（含立项审批流）建表脚本
-- 库：uni_research_fund
-- 说明：如果你之前已经执行过旧版 project_module.sql，建议先执行下面三句再重建：
-- DROP TABLE IF EXISTS project_audit_log;
-- DROP TABLE IF EXISTS project_member;
-- DROP TABLE IF EXISTS project;

CREATE TABLE IF NOT EXISTS project (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_code VARCHAR(64) NOT NULL,
  project_name VARCHAR(255) NOT NULL,
  project_type VARCHAR(64) DEFAULT NULL,
  principal_user_id BIGINT NOT NULL,
  unit_id BIGINT DEFAULT NULL,
  start_date DATE DEFAULT NULL,
  end_date DATE DEFAULT NULL,
  total_budget DECIMAL(18,2) DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 0 COMMENT '0草稿 1待二级单位审批 2待财务处审批 3立项通过 4已驳回 5已结题 6停用',
  description TEXT,
  submitted_at DATETIME DEFAULT NULL,
  unit_auditor_user_id BIGINT DEFAULT NULL,
  unit_audit_at DATETIME DEFAULT NULL,
  unit_audit_comment VARCHAR(500) DEFAULT NULL,
  finance_auditor_user_id BIGINT DEFAULT NULL,
  finance_audit_at DATETIME DEFAULT NULL,
  finance_audit_comment VARCHAR(500) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_project_code (project_code),
  KEY idx_project_principal (principal_user_id),
  KEY idx_project_unit (unit_id),
  KEY idx_project_status (status),
  CONSTRAINT fk_urf_project_principal_user FOREIGN KEY (principal_user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_urf_project_unit_org FOREIGN KEY (unit_id) REFERENCES org_unit(id),
  CONSTRAINT fk_urf_project_unit_auditor_user FOREIGN KEY (unit_auditor_user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_urf_project_finance_auditor_user FOREIGN KEY (finance_auditor_user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS project_member (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  member_role VARCHAR(64) DEFAULT '项目成员',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_project_member (project_id, user_id),
  KEY idx_pm_user (user_id),
  CONSTRAINT fk_urf_pm_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
  CONSTRAINT fk_urf_pm_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS project_audit_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  action VARCHAR(64) NOT NULL,
  from_status TINYINT DEFAULT NULL,
  to_status TINYINT DEFAULT NULL,
  operator_user_id BIGINT DEFAULT NULL,
  comment VARCHAR(500) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_pal_project (project_id),
  KEY idx_pal_operator (operator_user_id),
  CONSTRAINT fk_urf_pal_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
  CONSTRAINT fk_urf_pal_operator FOREIGN KEY (operator_user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 旧版状态兼容（如果是从上一版升级且保留了数据，可按需手动执行）
-- UPDATE project SET status = 3 WHERE status = 1;
-- UPDATE project SET status = 5 WHERE status = 2;
-- UPDATE project SET status = 6 WHERE status = 0;
