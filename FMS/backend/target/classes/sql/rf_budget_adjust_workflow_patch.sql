-- 预算调整单 + 通用审批中心补丁

-- 兼容说明：若你的现有 rf_budget_adjust_line 表没有 created_at 字段，
-- 当前后端代码已不再强依赖该字段，可直接使用现有表结构。
-- 如需补齐字段，可自行执行：
-- ALTER TABLE rf_budget_adjust_line ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE TABLE IF NOT EXISTS rf_budget_adjust (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  adjust_no VARCHAR(64) NOT NULL,
  project_id BIGINT NOT NULL,
  applicant_id BIGINT NOT NULL,
  unit_id BIGINT NOT NULL,
  reason VARCHAR(500) NOT NULL,
  total_delta DECIMAL(18,2) NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 0,
  current_node VARCHAR(64) DEFAULT NULL,
  last_comment VARCHAR(500) DEFAULT NULL,
  submitted_at DATETIME DEFAULT NULL,
  effective_at DATETIME DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_rf_budget_adjust_no (adjust_no),
  KEY idx_rf_budget_adjust_project (project_id),
  KEY idx_rf_budget_adjust_applicant (applicant_id),
  KEY idx_rf_budget_adjust_unit (unit_id),
  KEY idx_rf_budget_adjust_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS rf_budget_adjust_line (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  adjust_id BIGINT NOT NULL,
  subject_id BIGINT NOT NULL,
  delta_amount DECIMAL(18,2) NOT NULL,
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_rf_budget_adjust_line_adjust (adjust_id),
  KEY idx_rf_budget_adjust_line_subject (subject_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS wf_task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  biz_type VARCHAR(64) NOT NULL,
  biz_id BIGINT NOT NULL,
  node_code VARCHAR(64) NOT NULL,
  task_title VARCHAR(255) DEFAULT NULL,
  assignee_role_code VARCHAR(64) DEFAULT NULL,
  assignee_user_id BIGINT DEFAULT NULL,
  applicant_user_id BIGINT DEFAULT NULL,
  unit_id BIGINT DEFAULT NULL,
  task_status VARCHAR(32) NOT NULL DEFAULT 'TODO',
  completed_action VARCHAR(32) DEFAULT NULL,
  completed_by BIGINT DEFAULT NULL,
  completed_at DATETIME DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_wf_task_biz (biz_type, biz_id),
  KEY idx_wf_task_todo (task_status, assignee_role_code, node_code),
  KEY idx_wf_task_unit (unit_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS wf_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  biz_type VARCHAR(64) NOT NULL,
  biz_id BIGINT NOT NULL,
  node_code VARCHAR(64) DEFAULT NULL,
  action VARCHAR(64) DEFAULT NULL,
  actor_user_id BIGINT DEFAULT NULL,
  comment VARCHAR(1000) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_wf_log_biz (biz_type, biz_id),
  KEY idx_wf_log_node (node_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS msg_inbox (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  msg_type VARCHAR(64) DEFAULT NULL,
  title VARCHAR(255) NOT NULL,
  content VARCHAR(1000) NOT NULL,
  related_biz_type VARCHAR(64) DEFAULT NULL,
  related_biz_id BIGINT DEFAULT NULL,
  is_read TINYINT NOT NULL DEFAULT 0,
  read_at DATETIME DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_msg_inbox_user (user_id, is_read),
  KEY idx_msg_inbox_biz (related_biz_type, related_biz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
