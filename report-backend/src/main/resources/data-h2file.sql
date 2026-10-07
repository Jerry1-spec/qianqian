-- =====================================================================
-- H2 文件库初始数据（电脑版安装包专用）
-- 初始导师账号：13800000000 / Teacher@123（BCrypt 密文，可直接登录）
-- INSERT IGNORE：账号已存在时跳过，保证重复启动安全。
-- =====================================================================

INSERT IGNORE INTO `sys_user` (`username`, `real_name`, `grade`, `password`, `role`, `teacher_id`, `is_init_password`, `pwd_version`)
VALUES ('13800000000', '导师', NULL, '$2a$10$wS2ru2nlm37jopd4NF.fIOCk92g.YoU.4.qxuh69GjFwDK.asnCDC', 'teacher', NULL, 0, 0);
