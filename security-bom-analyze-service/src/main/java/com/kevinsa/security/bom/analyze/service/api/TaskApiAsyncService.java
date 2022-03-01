package com.kevinsa.security.bom.analyze.service.api;

import com.kevinsa.security.bom.analyze.enums.TaskStatusCode;
import com.kevinsa.security.bom.analyze.service.common.BizCommonService;
import com.kevinsa.security.bom.analyze.utils.EncryptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.kevinsa.security.bom.analyze.runner.task.JarPomParserTask;
import com.kevinsa.security.bom.analyze.utils.ExecUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Service
public class TaskApiAsyncService {

    private static final Logger logger = LoggerFactory.getLogger(TaskApiAsyncService.class);

    @Autowired
    private ExecUtils execUtils;

    @Autowired
    private BizCommonService bizCommonService;

    @Autowired
    private JarPomParserTask jarPomParserTask;

    @Autowired
    private EncryptUtils encryptUtils;

    private static final String gitRemoteUrlCmd = "git remote -v |awk '{print $2}' | head -n 1";
    private static final String gitBranchCmd = "git rev-parse --abbrev-ref HEAD";
    private static final String gitCommitIdCmd = "git log | grep commit |head -n1|awk '{print $2}'";


    @Async
    public void executeMvnPlugin(String basePath) {
        try {
            Map<String, String> gitInfo = getGitInfo(basePath);
            String cmd = String.format("mvn com.kevinsa.security.bom:security-bom-analyze-maven-plugin:1.0-SNAPSHOT:parser -DgitAddress=%s -Dbranch=%s -DcommitId=%s",
                    gitInfo.get("gitRemoteUrl"),
                    gitInfo.get("gitBranch"),
                    gitInfo.get("gitCommitId"));
            execUtils.exec(cmd, basePath);
        } catch (Exception e) {
            logger.error("executeMvnPlugin error", e);
        }
    }

    protected Map<String, String> getGitInfo(String basePath) throws IOException, InterruptedException {
        Map<String, String> gitInfo = new HashMap<>();
        gitInfo.put("gitRemoteUrl", execUtils.execOut(gitRemoteUrlCmd, basePath));
        gitInfo.put("gitBranch", execUtils.execOut(gitBranchCmd, basePath));
        gitInfo.put("gitCommitId", execUtils.execOut(gitCommitIdCmd, basePath));
        return gitInfo;
    }

    @Async
    public void executeJarParser(String basePath, String jarName) {
        String redisKey = encryptUtils.md5Encrypt(basePath + jarName);
        try {
            bizCommonService.updateStatusCode(TaskStatusCode.TASK_INIT, redisKey, "executeJarParser");
            jarPomParserTask.execute(basePath, jarName);
            bizCommonService.updateStatusCode(TaskStatusCode.TASK_DONE, redisKey, "executeJarParser");
        } catch (Exception e) {
            logger.error("executeJarParser error", e);
            bizCommonService.updateStatusCode(TaskStatusCode.TASK_ERROR, redisKey, "executeJarParser");
        }
    }
}
