package com.aegis.companion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// model/vo/ResponseResult.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ResponseResult<T> success(T data) {
        return ResponseResult.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .build();
    }

    public static <T> ResponseResult<T> success() {
        return ResponseResult.<T>builder()
                .code(200)
                .message("success")
                .data(null)
                .build();
    }

    public static ResponseResult<?> error(String message) {
        return error(400, message);
    }

    public static ResponseResult<?> error(Integer code, String message) {
        return ResponseResult.builder()
                .code(code)
                .message(message)
                .build();
    }

}
