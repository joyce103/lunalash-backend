-- 移除舊的「服務項目」單選功能，已完全被 operation_catalog_item 複選功能取代 (見 operation_catalog_multi_select.sql)
-- 沒有其他表格外鍵參照 service_item，可以直接刪除

DROP TABLE service_item;
