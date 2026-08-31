package com.report.controller;

import com.report.common.Result;
import com.report.config.RequireRole;
import com.report.dto.CreateStudentReq;
import com.report.dto.ReportDetailResp;
import com.report.dto.ReviewReq;
import com.report.dto.StudentItemResp;
import com.report.service.TeacherService;
import com.report.util.UserContext;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 导师接口。@RequireRole("teacher") 保证仅导师可访问，
 * Service 层再校验学生/周报归属当前导师。
 */
@RestController
@RequestMapping("/api/teacher")
@RequireRole("teacher")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping("/create-student")
    public Result<Map<String, Long>> createStudent(@Valid @RequestBody CreateStudentReq req) {
        Long studentId = teacherService.createStudent(UserContext.getUserId(), req);
        return Result.success(Map.of("studentId", studentId));
    }

    @GetMapping("/students")
    public Result<List<StudentItemResp>> students() {
        return Result.success(teacherService.listStudents(UserContext.getUserId()));
    }

    @GetMapping("/student/{stuId}/reports")
    public Result<List<ReportDetailResp>> studentReports(@PathVariable("stuId") Long stuId) {
        return Result.success(teacherService.listStudentReports(UserContext.getUserId(), stuId));
    }

    @PutMapping("/report/{reportId}/review")
    public Result<Void> review(@PathVariable("reportId") Long reportId, @Valid @RequestBody ReviewReq req) {
        teacherService.reviewReport(UserContext.getUserId(), reportId, req);
        return Result.success();
    }
}
