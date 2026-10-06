-- =====================================================================
-- PostgreSQL 初始数据（云端持久化使用）
-- 初始导师账号：
--   账号：13800000000
--   密码：Teacher@123
--   password 列为上面明文的 BCrypt(cost=10) 密文，可直接登录。
-- 使用 ON CONFLICT DO NOTHING 保证幂等：账号已存在时不做任何修改
-- （保留导师后续修改过的密码，避免每次重启被重置）。
-- =====================================================================

INSERT INTO sys_user (username, password, role, teacher_id, is_init_password, pwd_version)
VALUES ('13800000000', '$2a$10$wS2ru2nlm37jopd4NF.fIOCk92g.YoU.4.qxuh69GjFwDK.asnCDC', 'teacher', NULL, 0, 0)
ON CONFLICT (username) DO NOTHING;
