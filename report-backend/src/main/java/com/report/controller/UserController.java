package com.report.controller;

import com.report.common.Result;
import com.report.config.AllowInitPassword;
import com.report.dto.ChangePwdReq;
import com.report.dto.UserInfoResp;
import com.report.service.UserService;
import com.report.util.UserContext;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口：修改密码、获取当前用户信息。
 * 两个接口均标注 @AllowInitPassword，允许初始密码状态的用户访问（否则无法完成改密）。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @AllowInitPassword
    @PostMapping("/change-pwd")
    public Result<Void> changePwd(@Valid @RequestBody ChangePwdReq req) {
        userService.changePwd(UserContext.getUserId(), req);
        return Result.success();
    }

    @AllowInitPassword
    @GetMapping("/info")
    public Result<UserInfoResp> info() {
        return Result.success(userService.getUserInfo(UserContext.getUserId()));
    }
}
