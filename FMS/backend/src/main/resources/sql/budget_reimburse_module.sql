-- 预算与报销模块（最小可用版）
-- 库：uni_research_fund
-- 建议执行顺序：
-- 1) 先执行 project_module.sql
-- 2) 再执行本脚本

CREATE TABLE IF NOT EXISTS budget_subject (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT DEFAULT NULL,
  code VARCHAR(64) NOT NULL,
  name VARCHAR(100) NOT NULL,
  level_no INT NOT NULL DEFAULT 1,
  sort_no INT NOT NULL DEFAULT 0,
  enabled TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_budget_subject_code (code),
  KEY idx_budget_subject_parent (parent_id),
  CONSTRAINT fk_urf_budget_subject_parent FOREIGN KEY (parent_id) REFERENCES budget_subject(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO budget_subject(id, parent_id, code, name, level_no, sort_no, enabled)
VALUES (100, NULL, 'ROOT', '科研项目预算科目', 1, 1, 1)
ON DUPLICATE KEY UPDATE
  parent_id = VALUES(parent_id),
  name = VALUES(name),
  level_no = VALUES(level_no),
  sort_no = VALUES(sort_no),
  enabled = VALUES(enabled),
  updated_at = CURRENT_TIMESTAMP;

INSERT INTO budget_subject(id, parent_id, code, name, level_no, sort_no, enabled)
VALUES
  (110, 100, 'DEV', '业务费', 2, 10, 1),
  (111, 110, 'MAT', '材料费', 3, 11, 1),
  (112, 110, 'TEST', '测试化验加工费', 3, 12, 1),
  (113, 110, 'TRAVEL', '差旅/会议/国际合作交流费', 3, 13, 1),
  (114, 110, 'PUB', '出版/文献/信息传播/知识产权事务费', 3, 14, 1),
  (120, 100, 'EQ', '设备费', 2, 20, 1),
  (121, 120, 'EQ_BUY', '设备购置费', 3, 21, 1),
  (122, 120, 'EQ_TRY', '设备试制费', 3, 22, 1),
  (130, 100, 'LABOR', '劳务费', 2, 30, 1),
  (140, 100, 'CONSULT', '专家咨询费', 2, 40, 1),
  (190, 100, 'OTHER', '其他费用', 2, 90, 1)
ON DUPLICATE KEY UPDATE
  parent_id = VALUES(parent_id),
  name = VALUES(name),
  level_no = VALUES(level_no),
  sort_no = VALUES(sort_no),
  enabled = VALUES(enabled),
  updated_at = CURRENT_TIMESTAMP;

CREATE TABLE IF NOT EXISTS project_budget (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  subject_id BIGINT NOT NULL,
  approved_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
  used_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
  frozen_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_project_budget_pair (project_id, subject_id),
  KEY idx_project_budget_project (project_id),
  KEY idx_project_budget_subject (subject_id),
  CONSTRAINT fk_urf_project_budget_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
  CONSTRAINT fk_urf_project_budget_subject FOREIGN KEY (subject_id) REFERENCES budget_subject(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS reimburse (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reimburse_no VARCHAR(64) NOT NULL,
  project_id BIGINT NOT NULL,
  applicant_user_id BIGINT NOT NULL,
  unit_id BIGINT DEFAULT NULL,
  title VARCHAR(255) NOT NULL,
  total_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 0 COMMENT '0草稿 1待二级单位审批 2待财务处审批 3审批通过 4已驳回 5已支付 6已作废',
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
  UNIQUE KEY uk_reimburse_no (reimburse_no),
  KEY idx_reimburse_project (project_id),
  KEY idx_reimburse_applicant (applicant_user_id),
  KEY idx_reimburse_unit (unit_id),
  KEY idx_reimburse_status (status),
  CONSTRAINT fk_urf_reimburse_project FOREIGN KEY (project_id) REFERENCES project(id),
  CONSTRAINT fk_urf_reimburse_applicant FOREIGN KEY (applicant_user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_urf_reimburse_unit FOREIGN KEY (unit_id) REFERENCES org_unit(id),
  CONSTRAINT fk_urf_reimburse_unit_auditor FOREIGN KEY (unit_auditor_user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_urf_reimburse_finance_auditor FOREIGN KEY (finance_auditor_user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS reimburse_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reimburse_id BIGINT NOT NULL,
  subject_id BIGINT NOT NULL,
  item_name VARCHAR(255) NOT NULL,
  expense_date DATE DEFAULT NULL,
  amount DECIMAL(18,2) NOT NULL DEFAULT 0,
  remark VARCHAR(500) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_reimburse_item_reimburse (reimburse_id),
  KEY idx_reimburse_item_subject (subject_id),
  CONSTRAINT fk_urf_reimburse_item_reimburse FOREIGN KEY (reimburse_id) REFERENCES reimburse(id) ON DELETE CASCADE,
  CONSTRAINT fk_urf_reimburse_item_subject FOREIGN KEY (subject_id) REFERENCES budget_subject(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS reimburse_audit_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reimburse_id BIGINT NOT NULL,
  action VARCHAR(64) NOT NULL,
  from_status TINYINT DEFAULT NULL,
  to_status TINYINT DEFAULT NULL,
  operator_user_id BIGINT DEFAULT NULL,
  comment VARCHAR(500) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_reimburse_audit_log_reimburse (reimburse_id),
  KEY idx_reimburse_audit_log_operator (operator_user_id),
  CONSTRAINT fk_urf_reimburse_audit_log_reimburse FOREIGN KEY (reimburse_id) REFERENCES reimburse(id) ON DELETE CASCADE,
  CONSTRAINT fk_urf_reimburse_audit_log_operator FOREIGN KEY (operator_user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
