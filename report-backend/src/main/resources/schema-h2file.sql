-- =====================================================================
-- H2 文件库建表脚本（电脑版安装包专用，数据持久化、重启不丢）
-- 与 schema-h2.sql 字段/约束一致，但使用 IF NOT EXISTS：
-- 表已存在时跳过，不覆盖已有数据。
-- =====================================================================

CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(20) NOT NULL,
  `real_name` VARCHAR(50) DEFAULT NULL,
  `grade` VARCHAR(20) DEFAULT NULL,
  `password` VARCHAR(100) NOT NULL,
  `role` VARCHAR(16) NOT NULL,
  `teacher_id` BIGINT DEFAULT NULL,
  `is_init_password` TINYINT NOT NULL DEFAULT 1,
  `pwd_version` INT NOT NULL DEFAULT 0,
  `create_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `uk_username` UNIQUE (`username`)
);

CREATE TABLE IF NOT EXISTS `weekly_report` (
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
CREATE INDEX IF NOT EXISTS `idx_student_id` ON `weekly_report` (`student_id`);
