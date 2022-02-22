package com.kevinsa.security.bom.analyze.enums;

import lombok.Getter;

@Getter
public enum ApiResultCode {
    SUCCESS(1, "SUCCESS"),
    ERROR(0, "SERVER_ERROR");

    private final int code;
    private final String msg;

    ApiResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
