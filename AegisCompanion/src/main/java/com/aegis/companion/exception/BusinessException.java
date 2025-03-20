package com.aegis.companion.exception;

import lombok.Getter;

// exception/BusinessException.java
@Getter
public class BusinessException extends RuntimeException {
    // 可选：定义错误码（针对国际化、前端多语言提示等场景）
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400; // 默认400错误码，或根据需要扩展
    }

    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }

}
