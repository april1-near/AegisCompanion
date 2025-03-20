package com.aegis.companion.security;

import com.aegis.companion.service.FilterService;
import com.aegis.companion.service.impl.CustomUserDetailsService;
import com.aegis.companion.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
// 其他导入...

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_PREFIX = "ROLE_";
    private final FilterService filterService;
    private final CustomUserDetailsService detailsService;
    private final JwtUtil jwtUtil;


    private boolean isWebSocketHandshake(HttpServletRequest request) {
        String connectionHeader = request.getHeader("Connection");
        String upgradeHeader = request.getHeader("Upgrade");
        return "websocket".equalsIgnoreCase(upgradeHeader) &&
                connectionHeader != null &&
                connectionHeader.toLowerCase().contains("upgrade");
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (isWebSocketHandshake(request)) {
            log.info("⏩ 跳过 WebSocket 握手请求的 JWT 验证");
            filterChain.doFilter(request, response);
            return;
        }


        log.info("===== 开始处理请求 =====");
        log.info("请求路径: {}", request.getRequestURI());
        log.info("请求方法: {}", request.getMethod());


        try {
            log.info("🔑 尝试提取并验证 JWT...");
            String token = extractToken(request);
            if (token == null) {
                log.info("⚠️ 请求未携带令牌，跳过处理");
                filterChain.doFilter(request, response);
                return;
            }

            log.info("✅ 令牌提取成功，开始解析...");
            Claims claims = jwtUtil.parseToken(token);
            log.info("🔍 解析成功，令牌内容: 用户={}, 角色={}, 过期时间={}",
                    claims.getSubject(), claims.get("role"), claims.getExpiration());

            validateRequiredClaims(claims);
            log.info("🛡️ 必要声明校验通过");

            String username = claims.getSubject();
            Long id = claims.get("uId", Long.class);
            List<String> roles = getRolesFromClaims(claims);
            log.info("👤 用户主体信息: 用户名={},ID={}, 角色列表={}", username, id, roles);

            setSecurityContext(username, id, roles);
            log.info("🔒 用户认证上下文已设置");
        } catch (SignatureException ex) {
            log.info("❗ 令牌签名无效: {}", ex.getMessage());
            sendAuthError(response, "令牌签名无效", ex);
            return;
        } catch (ExpiredJwtException ex) {
            log.info("❗ 令牌已过期: {}", ex.getMessage());
            sendAuthError(response, "令牌已过期", ex);
            return;
        } catch (JwtUtil.JwtAuthenticationException | IllegalArgumentException ex) {
            log.info("❗ 令牌不合法: {}", ex.getMessage());
            sendAuthError(response, "无效的令牌", ex);
            return;
        } catch (Exception ex) {
            log.error("💥 JWT处理未知异常", ex);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return;
        }

        log.info("⏭️ 继续执行后续过滤器链...");
        filterChain.doFilter(request, response);
    }

    // JwtAuthFilter.java 修改提取逻辑
    private String extractToken(HttpServletRequest request) {

        // 普通 HTTP 请求处理
        log.info("🔧 尝试从请求头提取令牌...");
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            log.info("✅ 令牌提取成功（长度={}）", token.length());
            return token;
        }

        log.info("⛔ 未找到合法的 Authorization 头");
        return null;
    }


    private void validateRequiredClaims(Claims claims) {
        log.info("🔍 校验必要声明...");
        if (!StringUtils.hasText(claims.getSubject())) {
            log.info("⛔ 缺少用户主体标识（sub）");
            throw new JwtUtil.JwtAuthenticationException("缺失用户主体标识");
        }
        if (claims.get("uId") == null) {
            log.info("⛔ 缺少角色声明（uId）");
            throw new JwtUtil.JwtAuthenticationException("缺失角色声明");
        }
        if (claims.get("role") == null) {
            log.info("⛔ 缺少角色声明（role）");
            throw new JwtUtil.JwtAuthenticationException("缺失角色声明");
        }
        log.info("🆗 必要声明校验完成");
    }

    private List<String> getRolesFromClaims(Claims claims) {
        log.info("🔗 从声明中提取角色...");
        Object rolesClaim = claims.get("role");
        List<String> roles;

        if (rolesClaim instanceof String) {
            roles = Collections.singletonList((String) rolesClaim);
            log.info("📜 角色声明为单一字符串: {}", roles);
        } else if (rolesClaim instanceof List) {
            roles = ((List<?>) rolesClaim).stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .collect(Collectors.toList());
            log.info("📜 角色声明为列表: {}", roles);
        } else {
            log.info("❌ 无效的角色声明格式: {}", rolesClaim.getClass().getSimpleName());
            throw new JwtUtil.JwtAuthenticationException("无效的角色声明格式");
        }
        return roles;
    }

    private void setSecurityContext(String username, Long id, List<String> roles) {
        log.info("🛠️ 设置用户安全上下文...");

//        User user = filterService.loadUserWithValidation(username, id);

        UserDetails userDetails = detailsService.loadUserByUsername(username);


        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("🆗 安全上下文设置完成: 用户名={}, 权限={}", userDetails.getUsername(), userDetails.getAuthorities());
    }

    private void sendAuthError(HttpServletResponse response, String message, Exception ex) throws IOException {
        log.info("⛔ 发送认证错误响应: {}", message);
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer error=\"invalid_token\"");
        response.sendError(HttpStatus.UNAUTHORIZED.value(), message);
    }

    private boolean isPreflightRequest(HttpServletRequest request) {
        log.info("🔍 检查是否为预检请求...");
        boolean isPreflight = HttpMethod.OPTIONS.matches(request.getMethod()) &&
                request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD) != null;
        log.info("🔧 预检请求检测结果: {}", isPreflight);
        return isPreflight;
    }
}
