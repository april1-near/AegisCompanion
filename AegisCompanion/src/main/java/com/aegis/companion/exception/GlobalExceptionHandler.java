package com.aegis.companion.exception;

import com.aegis.companion.model.vo.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseResult<?>> handleBusinessException(BusinessException e) {
        log.error("用户异常", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseResult.error(e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseResult<?>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("鉴权异常:", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseResult.error("权限不足"));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseResult<?>> handleValidationException(MethodArgumentNotValidException ex) {

        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.error("数据验证异常:" + errorMsg);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseResult.error(errorMsg));

    }

    // GlobalExceptionHandler.java
    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ResponseResult<?>> handleJwtAuthException(JwtAuthenticationException e) {
        log.error("jwt异常", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseResult.error("error"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseResult<?>> handleException(Exception e) {
        log.error("全局异常", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseResult.error("error"));
    }


}
