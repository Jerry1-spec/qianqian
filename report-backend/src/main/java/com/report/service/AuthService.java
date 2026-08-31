package com.report.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.report.common.BizException;
import com.report.common.ErrorCode;
import com.report.dto.LoginReq;
import com.report.dto.LoginResp;
import com.report.entity.User;
import com.report.mapper.UserMapper;
import com.report.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务：账号密码登录。
 */
@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResp login(LoginReq req) {
        User user = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", req.getUsername()));
        // 账号不存在或密码错误统一返回 LOGIN_FAIL，避免枚举账号
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.LOGIN_FAIL);
        }
        String token = jwtUtil.generateToken(user.getId(), user.getRole(), user.getPwdVersion());
        boolean needChangePwd = Integer.valueOf(1).equals(user.getIsInitPassword());
        return new LoginResp(token, user.getRole(), user.getId(), needChangePwd);
    }
}
