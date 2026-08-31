package com.report.dto;

/**
 * 导师首页学生列表项：学生基本信息 + 最新周报状态。
 * latestStatus 为 null 表示该生尚无周报。
 */
public class StudentItemResp {

    private Long studentId;
    private String username;
    private String latestWeekYear;
    private String latestStatus;

    public StudentItemResp(Long studentId, String username, String latestWeekYear, String latestStatus) {
        this.studentId = studentId;
        this.username = username;
        this.latestWeekYear = latestWeekYear;
        this.latestStatus = latestStatus;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getUsername() {
        return username;
    }

    public String getLatestWeekYear() {
        return latestWeekYear;
    }

    public String getLatestStatus() {
        return latestStatus;
    }
}
