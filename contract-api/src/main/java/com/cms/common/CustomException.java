package com.cms.common;

public class CustomException extends RuntimeException {
    private Integer code;

    public CustomException(String message) {
        super(message);
        this.code = 500;
    }

    public CustomException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
