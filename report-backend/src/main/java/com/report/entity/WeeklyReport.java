package com.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("weekly_report")
public class WeeklyReport {

    @TableId(type = IdType.AUTO)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTeacherComment() {
        return teacherComment;
    }

    public void setTeacherComment(String teacherComment) {
        this.teacherComment = teacherComment;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getSubmitAt() {
        return submitAt;
    }

    public void setSubmitAt(LocalDateTime submitAt) {
        this.submitAt = submitAt;
    }

    public LocalDateTime getReviewAt() {
        return reviewAt;
    }

    public void setReviewAt(LocalDateTime reviewAt) {
        this.reviewAt = reviewAt;
    }
}
