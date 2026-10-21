-- 讓預約 (appointment) 可以關聯到會員 (member)：
-- 訪客送出預約時，系統會自動用電話比對現有會員，比對到就自動連結；比對不到就維持訪客身分 (member_id 為 NULL)。
-- 後台「預約管理」頁面可以手動重新連結/取消連結，不強制一定要有會員。

ALTER TABLE appointment
  ADD COLUMN member_id BIGINT NULL AFTER customer_phone,
  ADD CONSTRAINT fk_appointment_member FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE SET NULL;
