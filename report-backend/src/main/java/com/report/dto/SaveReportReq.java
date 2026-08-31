package com.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 保存周报请求（新建或保存草稿）。
 * id 为空 -> 新建；不为空 -> 更新已有草稿（业务层校验归属与状态）。
 * teacherComment/status/submitAt/reviewAt 等字段不接受前端传入，由后端控制。
 */
public class SaveReportReq {

    private Long id;

    @NotBlank(message = "周报周期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{1,2}$", message = "周报周期格式应为 年-周，例如 2026-34")
    private String weekYear;

    @NotBlank(message = "本周完成工作不能为空")
    private String contentWork;

    @NotBlank(message = "遇到的问题与难点不能为空")
    private String contentProblem;

    @NotBlank(message = "下周工作计划不能为空")
    private String contentNext;

    @NotBlank(message = "本周文献阅读总结不能为空")
    private String contentLiterature;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWeekYear() {
        return weekYear;
    }

    public void setWeekYear(String weekYear) {
        this.weekYear = weekYear;
    }

    public String getContentWork() {
        return contentWork;
    }

    public void setContentWork(String contentWork) {
        this.contentWork = contentWork;
    }

    public String getContentProblem() {
        return contentProblem;
    }

    public void setContentProblem(String contentProblem) {
        this.contentProblem = contentProblem;
    }

    public String getContentNext() {
        return contentNext;
    }

    public void setContentNext(String contentNext) {
        this.contentNext = contentNext;
    }

    public String getContentLiterature() {
        return contentLiterature;
    }

    public void setContentLiterature(String contentLiterature) {
        this.contentLiterature = contentLiterature;
    }
}
