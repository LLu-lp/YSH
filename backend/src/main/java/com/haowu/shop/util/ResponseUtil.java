package com.haowu.shop.util;

import lombok.Data;

@Data
public class ResponseUtil<T> {
    private int code;
    private String message;
    private T data;

    private ResponseUtil(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseUtil<T> success(T data) {
        return new ResponseUtil<>(200, "success", data);
    }

    public static <T> ResponseUtil<T> success(String message, T data) {
        return new ResponseUtil<>(200, message, data);
    }

    public static <T> ResponseUtil<T> error(int code, String message) {
        return new ResponseUtil<>(code, message, null);
    }

    public static <T> ResponseUtil<T> error(String message) {
        return new ResponseUtil<>(400, message, null);
    }

    public static <T> ResponseUtil<T> unauthorized() {
        return new ResponseUtil<>(401, "未登录或Token失效", null);
    }

    public static <T> ResponseUtil<T> forbidden() {
        return new ResponseUtil<>(403, "无操作权限", null);
    }

    public static <T> ResponseUtil<T> notFound() {
        return new ResponseUtil<>(404, "资源不存在", null);
    }

    public static <T> ResponseUtil<T> conflict(String message) {
        return new ResponseUtil<>(409, message, null);
    }

    public static <T> ResponseUtil<T> serverError() {
        return new ResponseUtil<>(500, "服务器内部错误", null);
    }
}