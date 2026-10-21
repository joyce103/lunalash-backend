-- 讓客人可以用「電話 + 查詢碼」自行查詢預約狀態：
-- 跟 appointment_member_link.sql 一樣，這份 SQL 是直接對正式資料庫執行，沒有走 Flyway migration
-- (這個專案的 Flyway 依賴實際上沒有在跑，詳見 admin_role_and_account_requests.sql 的說明)，
-- 僅留存作為紀錄與之後重建環境的依據。
-- 既有資料的 query_code 維持 NULL 即可 (舊預約沒有查詢碼，不補產生)，不會刪除任何資料。

ALTER TABLE appointment
  ADD COLUMN query_code VARCHAR(16) NULL
  COMMENT '客人查詢預約狀態用的查詢碼，8 碼，排除容易混淆的 0/O/1/I/L'
  AFTER line_user_id,
  ADD CONSTRAINT uq_appointment_query_code UNIQUE (query_code);
