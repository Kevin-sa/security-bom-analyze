package com.kevinsa.security.bom.analyze.controller;

import static com.kevinsa.security.bom.analyze.constant.ApplicationConstants.SCA_API_REST_PATH_PREFIX;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kevinsa.security.bom.analyze.enums.ApiResultCode;
import com.kevinsa.security.bom.analyze.service.api.TaskApiAsyncService;
import com.kevinsa.security.bom.analyze.utils.ResultUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(SCA_API_REST_PATH_PREFIX + "/execute")
public class TaskExecuteController {

    @Autowired
    private TaskApiAsyncService taskApiAsyncService;
    /**
     * 异步任务做执行
     */
    @RequestMapping(value = "/mvn/plugin")
    public Object executeMvnPlugin(@RequestParam String basePath) {
        try {
            Map<String, Object> result = ResultUtils.success();
            taskApiAsyncService.executeMvnPlugin(basePath);
            return result;
        } catch (Exception e) {
            log.error("executeMvnPlugin error", e);
            return ResultUtils.error(ApiResultCode.ERROR);
        }
    }

    @RequestMapping(value = "/mvn/jar")
    public Object executeJar(@RequestParam String basePath,
                             @RequestParam String jarName) {
        try {
            Map<String, Object> result = ResultUtils.success();
            taskApiAsyncService.executeJarParser(basePath, jarName);
            return result;
        } catch (Exception e) {
            log.error("executeMvnPlugin error", e);
            return ResultUtils.error(ApiResultCode.ERROR);
        }
    }
}
