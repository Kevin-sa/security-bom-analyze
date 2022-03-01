package com.kevinsa.security.bom.analyze.service.common;

import com.kevinsa.security.bom.analyze.enums.TaskStatusCode;

public interface BizCommonService {
    void updateStatusCode(TaskStatusCode code, String key, String position);

    Object getStatusCode(String key);
}
