package com.smartcommunity.smart_community_platform.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    // 需要放行的白名单路径
    private static final List<String> PERMIT_ALL_PATHS = Arrays.asList(
            "/ws/**", // webSocket端点放行
            "/topic/**",
            "/queue/**",

            "/api/v1/auth/**",         // 认证接口放行（登录/注册）
            "/swagger-ui/**",          // Swagger UI 接口文档
            "/v3/api-docs/**"       // OpenAPI 描述文档
    );

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF和CORS默认配置
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 无状态会话管理
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 请求授权配置
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMIT_ALL_PATHS.toArray(new String[0])).permitAll() // 白名单路径放行
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // 管理员接口
                        .requestMatchers("/api/v1/users/**").authenticated()
                        .requestMatchers("/api/v1/doctors/schedules").hasAnyRole("ADMIN", "MAINTENANCE") // 医生排班管理
                        .requestMatchers("/api/v1/doctors/**").permitAll() // 医生信息查询（公开）
                        .requestMatchers("/api/v1/Medical/doctors/**").permitAll() // 医生和排班查询（公开）
                        .anyRequest().authenticated() // 其他请求需要认证
                )

                // 添加JWT过滤器
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 修改CORS配置，确保支持WebSocket
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许更广泛的来源
        config.addAllowedOriginPattern("*");

        // 允许所有头部（简化配置）
        config.addAllowedHeader("*");

        // 支持的方法
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

//
//        config.setAllowedOriginPatterns(Arrays.asList(
//                "http://localhost:5173"
////                "https://your-production-domain.com"
//        ));

        // 启用凭证
        config.setAllowCredentials(true);

        config.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type",
                "Upgrade", "Connection",
                "Sec-WebSocket-Key", "Sec-WebSocket-Version",
                "Sec-WebSocket-Protocol", "Sec-WebSocket-Extensions"
        ));

        config.setExposedHeaders(Arrays.asList(
                "Upgrade", "Connection",
                "Sec-WebSocket-Accept",
                "Sec-WebSocket-Protocol"
        ));

        // 预检请求缓存时间 - 1小时
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // 辅助方法：检测是否为开发环境
    private boolean isDevelopmentEnvironment() {
        // 可以根据环境变量、配置文件或其他方式来判断
        String activeProfile = System.getProperty("spring.profiles.active");
        return activeProfile == null || activeProfile.contains("dev") || activeProfile.contains("local");
    }

    // 密码编码器
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
