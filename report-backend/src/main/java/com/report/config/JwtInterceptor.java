package com.report.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.report.common.BizException;
import com.report.common.ErrorCode;
import com.report.entity.User;
import com.report.mapper.UserMapper;
import com.report.util.JwtUtil;
import com.report.util.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器：
 * 1) 解析 Authorization: Bearer <token>；
 * 2) 校验 token 中 pv == 库中 pwd_version（改密后旧 token 失效）；
 * 3) 校验 @RequireRole 角色权限；
 * 4) 强制改密拦截：is_init_password=1 的用户，除放行接口外一律拒绝。
 * 详见《技术设计补充说明》1.4 / 第5节。
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public JwtInterceptor(JwtUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行非控制器方法（如静态资源）
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);

        Long userId;
        String role;
        Integer pv;
        try {
            Claims claims = jwtUtil.parse(token);
            userId = Long.valueOf(claims.getSubject());
            role = claims.get("role", String.class);
            pv = claims.get("pv", Integer.class);
        } catch (Exception e) {
            // 解析失败、subject 非数字、claim 类型异常等一律视为令牌无效
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        // 密码版本校验：改密后旧 token 立即失效
        if (pv == null || !pv.equals(user.getPwdVersion())) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }

        // 角色权限校验
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        if (requireRole != null && !requireRole.value().equals(role)) {
            throw new BizException(ErrorCode.FORBIDDEN);
        }

        // 强制改密拦截：初始密码用户仅可访问 @AllowInitPassword 标记的接口
        boolean allowInit = handlerMethod.hasMethodAnnotation(AllowInitPassword.class);
        if (Integer.valueOf(1).equals(user.getIsInitPassword()) && !allowInit) {
            throw new BizException(ErrorCode.FORBIDDEN, "请先修改初始密码");
        }

        UserContext.set(new UserContext.CurrentUser(userId, role));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
