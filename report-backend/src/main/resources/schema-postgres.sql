-- =====================================================================
-- PostgreSQL 建表脚本（Render 等云端持久化）
-- 字段/约束与 MySQL 版 init.sql 对齐；表名 sys_user 规避保留字。
-- 首次部署时由 postgres profile + DB_INIT_MODE=always 自动执行。
-- 使用 IF NOT EXISTS 保证重复执行安全（不会覆盖已有数据）。
-- =====================================================================

CREATE TABLE IF NOT EXISTS sys_user (
  id               BIGSERIAL    PRIMARY KEY,
  username         VARCHAR(20)  NOT NULL,
  password         VARCHAR(100) NOT NULL,
  role             VARCHAR(16)  NOT NULL,
  teacher_id       BIGINT,
  is_init_password SMALLINT     NOT NULL DEFAULT 1,
  pwd_version      INT          NOT NULL DEFAULT 0,
  create_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_username UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS weekly_report (
  id                 BIGSERIAL   PRIMARY KEY,
  student_id         BIGINT      NOT NULL,
  week_year          VARCHAR(20) NOT NULL,
  content_work       TEXT        NOT NULL,
  content_problem    TEXT        NOT NULL,
  content_next       TEXT        NOT NULL,
  content_literature TEXT        NOT NULL,
  status             VARCHAR(20) NOT NULL DEFAULT 'draft',
  teacher_comment    TEXT,
  create_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  submit_at          TIMESTAMP,
  review_at          TIMESTAMP,
  CONSTRAINT uk_student_week UNIQUE (student_id, week_year)
);

CREATE INDEX IF NOT EXISTS idx_student_id ON weekly_report (student_id);
