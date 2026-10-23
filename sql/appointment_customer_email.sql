-- 讓訪客可以用 Google 登入後，改用 email 建立/查詢預約 (跟 appointment_query_code.sql 的查詢碼查詢是平行的兩條路線)：
-- 跟 appointment_query_code.sql / appointment_member_link.sql 一樣，這份 SQL 是直接對正式資料庫執行，沒有走 Flyway migration
-- (這個專案的 Flyway 依賴實際上沒有在跑，詳見 admin_role_and_account_requests.sql 的說明)，
-- 僅留存作為紀錄與之後重建環境的依據。
-- 這個欄位只會在「訪客用 Google 登入後建立預約」時由後端自動填入已驗證過的 email，
-- 不是前端表單欄位、也不接受前端自己填值。既有資料的 customer_email 維持 NULL 即可，不會刪除任何資料。

ALTER TABLE appointment
  ADD COLUMN customer_email VARCHAR(255) NULL
  COMMENT '訪客 Google 登入後已驗證的 email，匿名預約維持 NULL'
  AFTER query_code;
