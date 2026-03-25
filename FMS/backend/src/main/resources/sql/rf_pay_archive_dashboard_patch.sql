-- 支付 / 归档 / 消息中心 / 看板补丁
-- 执行前请确认已完成 rf_* 主表切换

CREATE TABLE IF NOT EXISTS rf_payment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reimb_id BIGINT NOT NULL,
  pay_method VARCHAR(32) NOT NULL,
  voucher_no VARCHAR(64) DEFAULT NULL,
  pay_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
  paid_at DATETIME NOT NULL,
  operator_user_id BIGINT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_rf_payment_reimb (reimb_id),
  KEY idx_rf_payment_paid_at (paid_at),
  KEY idx_rf_payment_operator (operator_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS rf_archive (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reimb_id BIGINT NOT NULL,
  archive_no VARCHAR(64) NOT NULL,
  archive_path VARCHAR(255) DEFAULT NULL,
  operator_user_id BIGINT DEFAULT NULL,
  archived_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_rf_archive_reimb (reimb_id),
  UNIQUE KEY uk_rf_archive_no (archive_no),
  KEY idx_rf_archive_archived_at (archived_at),
  KEY idx_rf_archive_operator (operator_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
