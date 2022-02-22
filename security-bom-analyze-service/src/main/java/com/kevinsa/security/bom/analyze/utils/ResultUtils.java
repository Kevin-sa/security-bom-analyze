package com.kevinsa.security.bom.analyze.utils;

import com.kevinsa.security.bom.analyze.enums.ApiResultCode;

import java.util.HashMap;
import java.util.Map;

public class ResultUtils {

    private static final String STATUS = "status";
    private static final String MSG = "msg";

    public static Map<String, Object> success() {
        Map<String, Object> object = new HashMap<>();
        object.put(STATUS, ApiResultCode.SUCCESS.getCode());
        return object;
    }

    public static Map<String, Object> error(ApiResultCode apiResultCode) {
        Map<String, Object> object = new HashMap<>();
        object.put(STATUS, apiResultCode.getCode());
        object.put(MSG, apiResultCode.getMsg());
        return object;
    }
}
