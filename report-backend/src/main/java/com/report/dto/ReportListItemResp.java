package com.report.dto;

import com.report.entity.WeeklyReport;

import java.time.LocalDateTime;

/**
 * 周报列表项：周期、状态、创建/提交时间。
 */
public class ReportListItemResp {

    private Long id;
    private String weekYear;
    private String status;
    private LocalDateTime createAt;
    private LocalDateTime submitAt;

    public static ReportListItemResp of(WeeklyReport r) {
        ReportListItemResp resp = new ReportListItemResp();
        resp.id = r.getId();
        resp.weekYear = r.getWeekYear();
        resp.status = r.getStatus();
        resp.createAt = r.getCreateAt();
        resp.submitAt = r.getSubmitAt();
        return resp;
    }

    public Long getId() {
        return id;
    }

    public String getWeekYear() {
        return weekYear;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public LocalDateTime getSubmitAt() {
        return submitAt;
    }
}
