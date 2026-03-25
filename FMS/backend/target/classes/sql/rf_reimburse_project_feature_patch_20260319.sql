-- 2026-03-19 功能补丁
-- 覆盖：消息中心字段、支付归档操作人字段、报销差旅补助字段

-- 1) 消息中心字段对齐
ALTER TABLE msg_inbox
  ADD COLUMN IF NOT EXISTS related_biz_type VARCHAR(30) NULL AFTER content,
  ADD COLUMN IF NOT EXISTS related_biz_id BIGINT NULL AFTER related_biz_type,
  ADD COLUMN IF NOT EXISTS read_at DATETIME NULL AFTER is_read;

UPDATE msg_inbox
SET related_biz_type = COALESCE(related_biz_type, biz_type),
    related_biz_id = COALESCE(related_biz_id, biz_id)
WHERE (related_biz_type IS NULL OR related_biz_id IS NULL);

-- 2) 支付/归档表字段对齐
ALTER TABLE rf_payment
  ADD COLUMN IF NOT EXISTS operator_user_id BIGINT NULL AFTER paid_at,
  ADD COLUMN IF NOT EXISTS updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

UPDATE rf_payment
SET operator_user_id = COALESCE(operator_user_id, operator_id)
WHERE operator_user_id IS NULL;

ALTER TABLE rf_archive
  ADD COLUMN IF NOT EXISTS operator_user_id BIGINT NULL AFTER archive_path,
  ADD COLUMN IF NOT EXISTS updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

UPDATE rf_archive
SET operator_user_id = COALESCE(operator_user_id, operator_id)
WHERE operator_user_id IS NULL;

-- 3) 报销明细补充差旅补助字段
ALTER TABLE rf_reimb_line
  ADD COLUMN IF NOT EXISTS base_amount DECIMAL(18,2) NULL AFTER amount,
  ADD COLUMN IF NOT EXISTS travel_days INT NULL AFTER base_amount,
  ADD COLUMN IF NOT EXISTS subsidy_per_day DECIMAL(18,2) NULL AFTER travel_days,
  ADD COLUMN IF NOT EXISTS subsidy_amount DECIMAL(18,2) NULL AFTER subsidy_per_day;

UPDATE rf_reimb_line
SET base_amount = COALESCE(base_amount, amount)
WHERE base_amount IS NULL;

-- 4) 历史状态兼容（可选）
UPDATE rf_reimb
SET current_node = 'PAY_ARCHIVE'
WHERE current_node = 'WAIT_PAY';
