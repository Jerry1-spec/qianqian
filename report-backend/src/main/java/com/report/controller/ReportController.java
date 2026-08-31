package com.report.controller;

import com.report.common.Result;
import com.report.config.RequireRole;
import com.report.dto.ReportDetailResp;
import com.report.dto.ReportListItemResp;
import com.report.dto.SaveReportReq;
import com.report.service.ReportService;
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
 * 学生周报接口。@RequireRole("student") 保证仅学生可访问，
 * Service 层再校验每条周报归属当前学生。
 */
@RestController
@RequestMapping("/api/report")
@RequireRole("student")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/list")
    public Result<List<ReportListItemResp>> list() {
        return Result.success(reportService.list(UserContext.getUserId()));
    }

    @PostMapping("/save")
    public Result<Map<String, Long>> save(@Valid @RequestBody SaveReportReq req) {
        Long id = reportService.save(UserContext.getUserId(), req);
        return Result.success(Map.of("id", id));
    }

    @PostMapping("/save-and-submit")
    public Result<Map<String, Long>> saveAndSubmit(@Valid @RequestBody SaveReportReq req) {
        Long id = reportService.saveAndSubmit(UserContext.getUserId(), req);
        return Result.success(Map.of("id", id));
    }

    @PutMapping("/submit/{id}")
    public Result<Void> submit(@PathVariable("id") Long id) {
        reportService.submit(UserContext.getUserId(), id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<ReportDetailResp> detail(@PathVariable("id") Long id) {
        return Result.success(reportService.detail(UserContext.getUserId(), id));
    }
}
