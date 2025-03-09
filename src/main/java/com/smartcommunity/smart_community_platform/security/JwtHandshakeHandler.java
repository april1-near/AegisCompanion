package com.smartcommunity.smart_community_platform.security;

import com.smartcommunity.smart_community_platform.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

// file: security/JwtHandshakeHandler.java
public class JwtHandshakeHandler extends DefaultHandshakeHandler {
    private final JwtUtil jwtUtil;

    public JwtHandshakeHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        // 从请求参数获取token
        String token = ((ServletServerHttpRequest) request).getServletRequest()
                .getParameter("token");

        if (token != null && jwtUtil.validateAndParseToken(token)) {
            Claims claims = jwtUtil.parseToken(token);
            return new StompPrincipal(claims.getSubject()); // 返回用户身份
        }
        throw new AuthenticationCredentialsNotFoundException("无效凭证");
    }
}
