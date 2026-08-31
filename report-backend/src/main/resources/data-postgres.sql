-- =====================================================================
-- PostgreSQL 初始数据（云端）
-- 初始导师账号：账号 13800000000 / 密码 Teacher@123
-- password 为 Teacher@123 的 BCrypt(cost=10) 密文，可直接登录。
-- ON CONFLICT DO NOTHING：重复执行不会报错、不会重复插入。
-- =====================================================================

INSERT INTO sys_user (username, password, role, teacher_id, is_init_password, pwd_version)
VALUES ('13800000000', '$2a$10$wS2ru2nlm37jopd4NF.fIOCk92g.YoU.4.qxuh69GjFwDK.asnCDC', 'teacher', NULL, 0, 0)
ON CONFLICT (username) DO NOTHING;
