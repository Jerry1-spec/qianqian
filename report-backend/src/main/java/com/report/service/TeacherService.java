package com.report.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.report.common.BizException;
import com.report.common.ErrorCode;
import com.report.dto.CreateStudentReq;
import com.report.dto.ReportDetailResp;
import com.report.dto.ReviewReq;
import com.report.dto.StudentItemResp;
import com.report.entity.User;
import com.report.entity.WeeklyReport;
import com.report.mapper.UserMapper;
import com.report.mapper.WeeklyReportMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 导师服务：创建学生账号、学生总览、查看学生周报、批阅。
 * 权限：所有涉及学生/周报的操作均校验归属当前导师。
 */
@Service
public class TeacherService {

    private final UserMapper userMapper;
    private final WeeklyReportMapper reportMapper;
    private final PasswordEncoder passwordEncoder;

    public TeacherService(UserMapper userMapper, WeeklyReportMapper reportMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.reportMapper = reportMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 创建学生账号：
     * username=手机号，初始密码=手机号后6位(BCrypt)，role=student，
     * teacher_id=当前导师，is_init_password=1（需强制改密）。
     * 详见 PRD 4.3 / 技术补充 3.6。
     *
     * @return 新学生用户ID
     */
    public Long createStudent(Long teacherId, CreateStudentReq req) {
        String phone = req.getPhone();
        // 手机号唯一校验（账号不可重复创建）
        Long exist = userMapper.selectCount(
                new QueryWrapper<User>().eq("username", phone));
        if (exist != null && exist > 0) {
            throw new BizException(ErrorCode.CONFLICT, "该手机号已存在，不可重复创建");
        }

        User student = new User();
        student.setUsername(phone);
        student.setPassword(passwordEncoder.encode(phone.substring(phone.length() - 6)));
        student.setRole("student");
        student.setTeacherId(teacherId);
        student.setIsInitPassword(1);
        student.setPwdVersion(0);
        userMapper.insert(student);
        return student.getId();
    }

    /**
     * 名下学生列表，附带各生最新一份周报的周期与状态（无周报则为 null）。
     */
    public List<StudentItemResp> listStudents(Long teacherId) {
        List<User> students = userMapper.selectList(new QueryWrapper<User>()
                .eq("teacher_id", teacherId)
                .eq("role", "student")
                .orderByAsc("id"));
        return students.stream().map(s -> {
            WeeklyReport latest = reportMapper.selectOne(new QueryWrapper<WeeklyReport>()
                    .eq("student_id", s.getId())
                    .orderByDesc("create_at")
                    .last("limit 1"));
            String weekYear = latest == null ? null : latest.getWeekYear();
            String status = latest == null ? null : latest.getStatus();
            return new StudentItemResp(s.getId(), s.getUsername(), weekYear, status);
        }).toList();
    }

    /**
     * 查看指定学生的所有周报（完整详情，含评语），按创建时间倒序。
     * 校验该学生归属当前导师。
     */
    public List<ReportDetailResp> listStudentReports(Long teacherId, Long studentId) {
        ensureStudentBelongsToTeacher(teacherId, studentId);
        List<WeeklyReport> reports = reportMapper.selectList(new QueryWrapper<WeeklyReport>()
                .eq("student_id", studentId)
                .orderByDesc("create_at"));
        return reports.stream().map(ReportDetailResp::of).toList();
    }

    /**
     * 批阅周报：校验周报所属学生归属当前导师 + 仅 submitted 可批阅，
     * 写入评语、状态 submitted -> reviewed、记录批阅时间。
     */
    public void reviewReport(Long teacherId, Long reportId, ReviewReq req) {
        WeeklyReport report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new BizException(ErrorCode.FORBIDDEN);
        }
        ensureStudentBelongsToTeacher(teacherId, report.getStudentId());
        if (!"submitted".equals(report.getStatus())) {
            throw new BizException(ErrorCode.FORBIDDEN, "仅待批阅周报可批阅");
        }
        report.setTeacherComment(req.getComment());
        report.setStatus("reviewed");
        report.setReviewAt(LocalDateTime.now());
        reportMapper.updateById(report);
    }

    /**
     * 校验目标学生存在、为学生角色且归属当前导师，否则 403。
     */
    private void ensureStudentBelongsToTeacher(Long teacherId, Long studentId) {
        User student = userMapper.selectById(studentId);
        if (student == null
                || !"student".equals(student.getRole())
                || !teacherId.equals(student.getTeacherId())) {
            throw new BizException(ErrorCode.FORBIDDEN);
        }
    }
}
