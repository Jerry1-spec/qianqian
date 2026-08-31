-- =====================================================================
-- 研究生周报助手 V1.0 数据库初始化脚本
-- 依据：《研究生周报助手 V1.0 产品需求文档（PRD）》第6节
--      《研究生周报助手 V1.0 技术设计补充说明》第2节
-- 执行方式：在 MySQL 8.0 中执行本脚本
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `report_assistant`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE `report_assistant`;

-- ---------------------------------------------------------------------
-- 用户表（含 pwd_version：改密后 +1 使旧 token 失效）
-- 表名用 sys_user 而非 user，规避 MySQL/PostgreSQL/H2 的保留字冲突
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(20) NOT NULL COMMENT '登录账号（手机号）',
  `password` varchar(100) NOT NULL COMMENT 'BCrypt加密密码',
  `role` varchar(16) NOT NULL COMMENT '角色：student/teacher',
  `teacher_id` bigint DEFAULT NULL COMMENT '学生所属导师ID，导师为空',
  `is_init_password` tinyint(1) NOT NULL DEFAULT 1 COMMENT '1=初始密码需改密，0=已修改',
  `pwd_version` int NOT NULL DEFAULT 0 COMMENT '密码版本号，改密后+1使旧token失效',
  `create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ---------------------------------------------------------------------
-- 学生周报表（含 uk_student_week：同一学生同一周期唯一）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `weekly_report`;
CREATE TABLE `weekly_report` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生用户ID',
  `week_year` varchar(20) NOT NULL COMMENT '周报周期 例：2026-34',
  `content_work` text NOT NULL COMMENT '本周工作内容',
  `content_problem` text NOT NULL COMMENT '遇到的问题',
  `content_next` text NOT NULL COMMENT '下周计划',
  `content_literature` text NOT NULL COMMENT '文献阅读情况',
  `status` varchar(20) NOT NULL DEFAULT 'draft' COMMENT 'draft/submitted/reviewed',
  `teacher_comment` text DEFAULT NULL COMMENT '导师批阅评语',
  `create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `submit_at` datetime DEFAULT NULL COMMENT '提交时间',
  `review_at` datetime DEFAULT NULL COMMENT '批阅时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  UNIQUE KEY `uk_student_week` (`student_id`, `week_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生周报表';

-- ---------------------------------------------------------------------
-- 初始导师账号（后台手动创建，V1.0 无前台创建导师能力）
-- 账号：13800000000  初始密码：Teacher@123
-- 下面 password 为 Teacher@123 的 BCrypt 密文（已用项目 BCryptPasswordEncoder 实测匹配）。
-- 如需自定义密码：运行后端 util.PasswordHashTool 生成对应 BCrypt 密文后替换，切勿写入明文。
-- 导师 role=teacher，teacher_id=NULL，is_init_password=0（导师无强制改密）。
-- ---------------------------------------------------------------------
INSERT INTO `sys_user` (`username`, `password`, `role`, `teacher_id`, `is_init_password`, `pwd_version`)
VALUES ('13800000000', '$2a$10$wS2ru2nlm37jopd4NF.fIOCk92g.YoU.4.qxuh69GjFwDK.asnCDC', 'teacher', NULL, 0, 0);
