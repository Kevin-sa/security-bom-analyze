package com.kevinsa.security.bom.analyze.service.common;

import org.apache.maven.model.Model;

public interface MavenCommonService {
    /**
     * todo: version版本号在properties中获取的方式
     * @param baseDir
     * @param type
     * @return
     * @throws Exception
     */
    Model getPomInfo(String baseDir, String type) throws Exception;
}
