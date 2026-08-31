-- =====================================================================
-- H2 内存库初始数据（本地演示专用）
-- 初始导师账号：
--   账号：13800000000
--   密码：Teacher@123
--   password 列为上面明文的 BCrypt(cost=10) 密文，可直接登录。
-- 导师 role=teacher，teacher_id=NULL，is_init_password=0（无强制改密）。
-- =====================================================================

INSERT INTO `sys_user` (`username`, `password`, `role`, `teacher_id`, `is_init_password`, `pwd_version`)
VALUES ('13800000000', '$2a$10$wS2ru2nlm37jopd4NF.fIOCk92g.YoU.4.qxuh69GjFwDK.asnCDC', 'teacher', NULL, 0, 0);
