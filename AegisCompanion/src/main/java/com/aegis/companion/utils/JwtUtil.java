package com.aegis.companion.utils;

import com.aegis.companion.model.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@ConfigurationProperties(prefix = "jwt")
@Data
@Slf4j
public class JwtUtil {

    private String secret;
    private Long expiration;
    private String issuer;
    private String header;
    private String prefix;

    // 生成JWT令牌
    public String generateToken(String username, Long uId, Role role) {
        String token = Jwts.builder()
                .issuer(issuer)
                .subject(username)
                .claim("uId", uId) // 用户ID
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();

        log.info("✅ 生成 JWT 令牌: {} |用户：{}", token, username); // 添加此行
        return token;

    }

    // 解析并验证JWT
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 验证JWT有效性
    public boolean validateAndParseToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException ex) {
            throw new JwtAuthenticationException("Token expired");
        } catch (UnsupportedJwtException | MalformedJwtException ex) {
            throw new JwtAuthenticationException("Invalid token format");
        } catch (IllegalArgumentException ex) {
            throw new JwtAuthenticationException("Empty token");
        }
    }

    // 生成签名密钥
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    // 自定义异常类
    public static class JwtAuthenticationException extends RuntimeException {
        public JwtAuthenticationException(String message) {
            super(message);
        }
    }
}
