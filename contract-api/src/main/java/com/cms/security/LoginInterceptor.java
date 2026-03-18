package com.cms.security;

import com.cms.common.CustomException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();

    public static Long getCurrentId() {
        return THREAD_LOCAL.get();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Claims claims = JwtUtil.parseToken(token);
                Long userId = Long.valueOf(claims.get("userId").toString());
                THREAD_LOCAL.set(userId);
                return true;
            } catch (Exception e) {
                throw new CustomException(401, "Token已失效或不合法，请重新登录");
            }
        }
        throw new CustomException(401, "未登录或Token为空");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        THREAD_LOCAL.remove();
    }
}

