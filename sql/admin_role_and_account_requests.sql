-- 新增「最高管理員審核」功能所需的資料庫異動
-- 這個專案目前的資料表都是手動建立的（Flyway 依賴有加但實際上沒有在跑，
-- backend log 裡完全沒有 Flyway 相關訊息，資料庫也沒有 flyway_schema_history 表），
-- 所以這份 SQL 是直接對正式資料庫執行，沒有走 Flyway migration，僅留存作為紀錄與之後重建環境的依據。

ALTER TABLE admin
  ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'ADMIN'
  COMMENT '帳號權限層級：ADMIN 一般管理員 / SUPER_ADMIN 最高管理員'
  AFTER name;

CREATE TABLE admin_account_request (
  request_id BIGINT NOT NULL AUTO_INCREMENT,
  request_type VARCHAR(20) NOT NULL COMMENT 'REGISTER 新增帳號 / DELETE 刪除帳號',
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING 待審核 / APPROVED 已核准 / REJECTED 已駁回',
  requested_by_admin_id BIGINT NOT NULL,
  requested_by_username VARCHAR(50) NOT NULL,
  target_admin_id BIGINT NULL COMMENT 'DELETE 專用：欲刪除的帳號 ID',
  target_username VARCHAR(50) NULL,
  target_name VARCHAR(50) NULL,
  new_username VARCHAR(50) NULL COMMENT 'REGISTER 專用：欲新增的帳號',
  new_password_hash VARCHAR(255) NULL COMMENT 'REGISTER 專用：已用 BCrypt 加密過的密碼，核准時直接沿用',
  new_name VARCHAR(50) NULL,
  reviewed_by_admin_id BIGINT NULL,
  reviewed_by_username VARCHAR(50) NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  reviewed_at DATETIME NULL,
  PRIMARY KEY (request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 設定最高管理員時手動執行（先確認帳號名稱）：
-- UPDATE admin SET role = 'SUPER_ADMIN' WHERE username = '<你要指定的帳號>';
