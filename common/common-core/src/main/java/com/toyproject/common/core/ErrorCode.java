package com.toyproject.common.core;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_INPUT("COMMON-400", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    RESOURCE_NOT_FOUND("COMMON-404", HttpStatus.NOT_FOUND, "대상을 찾을 수 없습니다."),
    INTERNAL_ERROR("COMMON-500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

