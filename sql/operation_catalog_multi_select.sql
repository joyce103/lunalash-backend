-- 新增「操作項目」複選功能：訪客預約時可複選多個操作項目，依時間加總判斷可選時段
-- 跟其他 sql/ 底下的檔案一樣，是直接對正式資料庫執行，沒有走 Flyway (詳見 admin_role_and_account_requests.sql 的說明)

-- appointment 表目前只有幾筆開發測試時建立的資料 (customer_name = 'test' 等)，
-- 且下面要把 service_item_id 單選改成操作項目複選，新舊資料結構無法對應，所以先清空
DELETE FROM appointment;

-- 操作項目目錄：訪客預約複選用，跟 service_item (原本單選、現在預約表單已不使用) 是不同概念
CREATE TABLE operation_catalog_item (
  operation_catalog_item_id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  duration_minutes INT NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (operation_catalog_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 一筆預約可以複選多個操作項目
CREATE TABLE appointment_operation_item (
  appointment_id BIGINT NOT NULL,
  operation_catalog_item_id BIGINT NOT NULL,
  PRIMARY KEY (appointment_id, operation_catalog_item_id),
  CONSTRAINT fk_aoi_appointment FOREIGN KEY (appointment_id) REFERENCES appointment (appointment_id) ON DELETE CASCADE,
  CONSTRAINT fk_aoi_operation_item FOREIGN KEY (operation_catalog_item_id) REFERENCES operation_catalog_item (operation_catalog_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 核准預約時，把這次預約占用的每一個固定時段都各寫一筆進來，(slot_date, slot_time) 唯一索引防止 race condition。
-- 取代原本 appointment.approved_slot_key 生成欄位的做法，因為現在一筆預約可能因為操作項目時間加總橫跨好幾個固定時段，
-- 不再是「一筆預約對應一個時段」，沒辦法用單一生成欄位表示。
CREATE TABLE appointment_slot_lock (
  appointment_slot_lock_id BIGINT NOT NULL AUTO_INCREMENT,
  slot_date DATE NOT NULL,
  slot_time TIME NOT NULL,
  appointment_id BIGINT NOT NULL,
  PRIMARY KEY (appointment_slot_lock_id),
  UNIQUE KEY uq_slot_lock (slot_date, slot_time),
  CONSTRAINT fk_slot_lock_appointment FOREIGN KEY (appointment_id) REFERENCES appointment (appointment_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- appointment 表：移除舊的單選服務關聯與生成欄位，改成時間加總快照欄位
ALTER TABLE appointment
  DROP FOREIGN KEY fk_appointment_service,
  DROP INDEX uq_approved_slot,
  DROP COLUMN approved_slot_key,
  DROP COLUMN service_item_id,
  ADD COLUMN total_duration_minutes INT NOT NULL AFTER appointment_time;

-- Seed data：示範幾個操作項目，方便測試與展示後台的操作項目管理頁面
INSERT INTO operation_catalog_item (name, duration_minutes, sort_order, is_active) VALUES
  ('單根嫁接', 90, 1, 1),
  ('Fluffy 嫁接', 120, 2, 1),
  ('補睫（3 週內）', 60, 3, 1),
  ('卸睫', 30, 4, 1);
