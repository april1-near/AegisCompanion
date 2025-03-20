package com.aegis.companion.exception;

// exception/JwtAuthenticationException.java
public class JwtAuthenticationException extends RuntimeException {
    public JwtAuthenticationException(String message) {
        super(message);  // 直接传递错误信息
    }

    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);  // 携带原始异常
    }
}
