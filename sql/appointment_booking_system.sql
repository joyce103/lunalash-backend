-- 新增「預約系統」所需的資料庫異動
-- 跟 admin_role_and_account_requests.sql 一樣，這份 SQL 是直接對正式資料庫執行，沒有走 Flyway migration
-- (這個專案的 Flyway 依賴實際上沒有在跑，詳見 admin_role_and_account_requests.sql 的說明)，
-- 僅留存作為紀錄與之後重建環境的依據。

CREATE TABLE service_item (
  service_item_id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (service_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE available_date (
  available_date_id BIGINT NOT NULL AUTO_INCREMENT,
  appointment_date DATE NOT NULL,
  is_open TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (available_date_id),
  UNIQUE KEY uq_available_date (appointment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE available_slot (
  available_slot_id BIGINT NOT NULL AUTO_INCREMENT,
  available_date_id BIGINT NOT NULL,
  slot_time TIME NOT NULL,
  is_open TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (available_slot_id),
  UNIQUE KEY uq_date_time (available_date_id, slot_time),
  CONSTRAINT fk_slot_date FOREIGN KEY (available_date_id) REFERENCES available_date (available_date_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE appointment (
  appointment_id BIGINT NOT NULL AUTO_INCREMENT,
  service_item_id BIGINT NOT NULL,
  customer_name VARCHAR(50) NOT NULL,
  customer_phone VARCHAR(20) NOT NULL,
  line_user_id VARCHAR(100) NULL COMMENT '預留給之後串接 LINE Login / LIFF / Messaging API，目前一律是 NULL',
  appointment_date DATE NOT NULL,
  appointment_time TIME NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING 待審核 / APPROVED 已核准 / REJECTED 已拒絕',
  -- 這個生成欄位只有在 status = APPROVED 時才會有值，並加上唯一索引：
  -- 讓「同一個日期+時段最多只能有一筆 APPROVED」直接由資料庫保證，
  -- 兩個管理員同時核准同一個時段時，第二個一定會因為違反唯一索引而失敗 (由 AppointmentService.approve 捕捉並轉成友善訊息)，
  -- 不會有 race condition。MySQL 的 UNIQUE KEY 允許多個 NULL 值並存，所以 PENDING/REJECTED 的資料完全不受影響。
  approved_slot_key VARCHAR(30) GENERATED ALWAYS AS (
    CASE WHEN status = 'APPROVED' THEN CONCAT(appointment_date, '_', appointment_time) ELSE NULL END
  ) STORED,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (appointment_id),
  UNIQUE KEY uq_approved_slot (approved_slot_key),
  CONSTRAINT fk_appointment_service FOREIGN KEY (service_item_id) REFERENCES service_item (service_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed data：先給幾個示範服務項目，方便測試與展示後台的服務管理頁面。
-- 沒有 seed 任何 available_date / available_slot，因為哪些日期時段開放屬於業主的實際營業決策，
-- 不應該由程式先塞假資料造成誤解，請直接在後台「預約時段管理」頁面設定。
INSERT INTO service_item (name, sort_order, is_active) VALUES
  ('基礎款單根嫁接', 1, 1),
  ('進階款 Fluffy 嫁接', 2, 1),
  ('補睫（3 週內）', 3, 1);
