-- =====================================================================
-- H2 内存库建表脚本（本地演示专用，字段/约束与 MySQL 版 init.sql 对齐）
-- 由 application-h2.yml 的 spring.sql.init 在启动时自动执行
-- =====================================================================

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(20) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `role` VARCHAR(16) NOT NULL,
  `teacher_id` BIGINT DEFAULT NULL,
  `is_init_password` TINYINT NOT NULL DEFAULT 1,
  `pwd_version` INT NOT NULL DEFAULT 0,
  `create_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `uk_username` UNIQUE (`username`)
);

DROP TABLE IF EXISTS `weekly_report`;
CREATE TABLE `weekly_report` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `student_id` BIGINT NOT NULL,
  `week_year` VARCHAR(20) NOT NULL,
  `content_work` TEXT NOT NULL,
  `content_problem` TEXT NOT NULL,
  `content_next` TEXT NOT NULL,
  `content_literature` TEXT NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'draft',
  `teacher_comment` TEXT DEFAULT NULL,
  `create_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `submit_at` DATETIME DEFAULT NULL,
  `review_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `uk_student_week` UNIQUE (`student_id`, `week_year`)
);
CREATE INDEX `idx_student_id` ON `weekly_report` (`student_id`);
