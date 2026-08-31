package com.report.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.report.common.BizException;
import com.report.common.ErrorCode;
import com.report.dto.ReportDetailResp;
import com.report.dto.ReportListItemResp;
import com.report.dto.SaveReportReq;
import com.report.entity.WeeklyReport;
import com.report.mapper.WeeklyReportMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生周报服务：新建/保存草稿、提交、列表、详情。
 * 权限：所有操作均校验周报归属当前学生。
 */
@Service
public class ReportService {

    private final WeeklyReportMapper reportMapper;

    public ReportService(WeeklyReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    /**
     * 保存周报：有 id -> 更新（校验归属 + 仅 draft 可改）；无 id -> 查重后新建。
     * 详见技术补充 1.3。
     *
     * @return 周报ID
     */
    public Long save(Long studentId, SaveReportReq req) {
        if (req.getId() != null) {
            // 更新已有草稿
            WeeklyReport existing = reportMapper.selectById(req.getId());
            if (existing == null || !existing.getStudentId().equals(studentId)) {
                throw new BizException(ErrorCode.FORBIDDEN);
            }
            if (!"draft".equals(existing.getStatus())) {
                throw new BizException(ErrorCode.FORBIDDEN, "已提交周报不可修改");
            }
            existing.setWeekYear(req.getWeekYear());
            existing.setContentWork(req.getContentWork());
            existing.setContentProblem(req.getContentProblem());
            existing.setContentNext(req.getContentNext());
            existing.setContentLiterature(req.getContentLiterature());
            reportMapper.updateById(existing);
            return existing.getId();
        }

        // 新建：同一学生同一周期唯一
        Long dup = reportMapper.selectCount(new QueryWrapper<WeeklyReport>()
                .eq("student_id", studentId)
                .eq("week_year", req.getWeekYear()));
        if (dup != null && dup > 0) {
            throw new BizException(ErrorCode.CONFLICT, "该周期周报已存在");
        }
        WeeklyReport report = new WeeklyReport();
        report.setStudentId(studentId);
        report.setWeekYear(req.getWeekYear());
        report.setContentWork(req.getContentWork());
        report.setContentProblem(req.getContentProblem());
        report.setContentNext(req.getContentNext());
        report.setContentLiterature(req.getContentLiterature());
        report.setStatus("draft");
        reportMapper.insert(report);
        return report.getId();
    }

    /**
     * 保存并提交：一次请求内完成保存最新内容 + 提交，避免前端两次请求的非原子问题。
     * 复用 save 的新建/更新与归属、状态校验逻辑。
     *
     * @return 周报ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveAndSubmit(Long studentId, SaveReportReq req) {
        Long id = save(studentId, req);
        submit(studentId, id);
        return id;
    }

    /**
     * 提交周报：校验归属 + 仅 draft 可提交，状态 draft -> submitted。
     */
    public void submit(Long studentId, Long reportId) {
        WeeklyReport report = reportMapper.selectById(reportId);
        if (report == null || !report.getStudentId().equals(studentId)) {
            throw new BizException(ErrorCode.FORBIDDEN);
        }
        if (!"draft".equals(report.getStatus())) {
            throw new BizException(ErrorCode.FORBIDDEN, "该周报已提交，不可重复提交");
        }
        report.setStatus("submitted");
        report.setSubmitAt(LocalDateTime.now());
        reportMapper.updateById(report);
    }

    /**
     * 个人周报列表，按创建时间倒序。
     */
    public List<ReportListItemResp> list(Long studentId) {
        List<WeeklyReport> reports = reportMapper.selectList(new QueryWrapper<WeeklyReport>()
                .eq("student_id", studentId)
                .orderByDesc("create_at"));
        return reports.stream().map(ReportListItemResp::of).toList();
    }

    /**
     * 周报详情，校验归属当前学生。
     */
    public ReportDetailResp detail(Long studentId, Long reportId) {
        WeeklyReport report = reportMapper.selectById(reportId);
        if (report == null || !report.getStudentId().equals(studentId)) {
            throw new BizException(ErrorCode.FORBIDDEN);
        }
        return ReportDetailResp.of(report);
    }
}
