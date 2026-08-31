package com.report.service;

import com.report.common.BizException;
import com.report.common.ErrorCode;
import com.report.dto.ChangePwdReq;
import com.report.dto.UserInfoResp;
import com.report.entity.User;
import com.report.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务：修改密码、获取当前用户信息。
 */
@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 修改密码：校验旧密码 -> 加密新密码 -> is_init_password=0 -> pwd_version+1（使旧token失效）。
     */
    public void changePwd(Long userId, ChangePwdReq req) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.FORBIDDEN, "旧密码不正确");
        }
        if (req.getOldPassword().equals(req.getNewPassword())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "新密码不能与旧密码相同");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        user.setIsInitPassword(0);
        user.setPwdVersion(user.getPwdVersion() + 1);
        userMapper.updateById(user);
    }

    public UserInfoResp getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        boolean needChangePwd = Integer.valueOf(1).equals(user.getIsInitPassword());
        return new UserInfoResp(user.getId(), user.getUsername(), user.getRole(), needChangePwd);
    }
}
