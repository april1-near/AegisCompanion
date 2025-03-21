// file: security/WebSocketAuthInterceptor.java
package com.aegis.companion.security;

import com.aegis.companion.exception.JwtAuthenticationException;
import com.aegis.companion.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * WebSocket认证拦截器
 * 用于在握手阶段进行用户身份验证
 */
@Slf4j
@RequiredArgsConstructor
public class WebSocketAuthInterceptor extends DefaultHandshakeHandler {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService detailsService;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest req = servletRequest.getServletRequest();

            // 多途径获取令牌
            String token = extractTokenFromRequest(req);
            log.info("🔐 WebSocket 握手令牌检测: {}", token);

            Claims claims = jwtUtil.parseToken(token);
            log.info("✅ 用户 {} 通过 WebSocket 认证", claims.get("uId"));


            // 加载完整的UserDetails
            UserDetails userDetails = detailsService.loadUserByUsername(claims.getSubject());
            // 设置SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
            );


            return (Principal) userDetails;
        } catch (Exception e) {
            log.error("⛔ WebSocket 握手认证失败", e);
            throw new JwtAuthenticationException("Authentication failed");
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        // 优先级1：查询参数
        String token = request.getParameter("token");
        if (StringUtils.hasText(token)) return token;

        // 优先级2：Authorization 头
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        throw new IllegalArgumentException("Missing authentication token");
    }

}
