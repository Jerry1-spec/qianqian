package com.report.dto;

import com.report.entity.WeeklyReport;

import java.time.LocalDateTime;

/**
 * 周报详情：完整内容 + 导师评语（如有）。
 */
public class ReportDetailResp {

    private Long id;
    private Long studentId;
    private String weekYear;
    private String contentWork;
    private String contentProblem;
    private String contentNext;
    private String contentLiterature;
    private String status;
    private String teacherComment;
    private LocalDateTime createAt;
    private LocalDateTime submitAt;
    private LocalDateTime reviewAt;

    public static ReportDetailResp of(WeeklyReport r) {
        ReportDetailResp resp = new ReportDetailResp();
        resp.id = r.getId();
        resp.studentId = r.getStudentId();
        resp.weekYear = r.getWeekYear();
        resp.contentWork = r.getContentWork();
        resp.contentProblem = r.getContentProblem();
        resp.contentNext = r.getContentNext();
        resp.contentLiterature = r.getContentLiterature();
        resp.status = r.getStatus();
        resp.teacherComment = r.getTeacherComment();
        resp.createAt = r.getCreateAt();
        resp.submitAt = r.getSubmitAt();
        resp.reviewAt = r.getReviewAt();
        return resp;
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getWeekYear() {
        return weekYear;
    }

    public String getContentWork() {
        return contentWork;
    }

    public String getContentProblem() {
        return contentProblem;
    }

    public String getContentNext() {
        return contentNext;
    }

    public String getContentLiterature() {
        return contentLiterature;
    }

    public String getStatus() {
        return status;
    }

    public String getTeacherComment() {
        return teacherComment;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public LocalDateTime getSubmitAt() {
        return submitAt;
    }

    public LocalDateTime getReviewAt() {
        return reviewAt;
    }
}
