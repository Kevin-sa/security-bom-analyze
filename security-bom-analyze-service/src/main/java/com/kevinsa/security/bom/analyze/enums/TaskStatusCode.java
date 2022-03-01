package com.kevinsa.security.bom.analyze.enums;

import lombok.Getter;

@Getter
public enum TaskStatusCode {
    TASK_INIT(1, "task_init"),
    TASK_DOING(2, "task_doing"),
    TASK_DONE(3, "task_done"),
    TASK_ERROR(0, "task_error");

    private final int code;
    private final String msg;

    TaskStatusCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
