package com.kevinsa.security.bom.analyze.service.common;

import com.kevinsa.security.bom.analyze.vo.mvnPlugin.ArtifactVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.JarMavenVO;
import org.apache.maven.model.Model;

import java.io.File;
import java.util.List;

public interface MavenCommonService {
    /**
     * todo: version版本号在properties中获取的方式
     * @param baseDir
     * @param type
     * @return
     * @throws Exception
     */
    Model getPomInfo(String baseDir, String type) throws Exception;

    JarMavenVO getJarMvnInfoByPom(File file, String type, List<ArtifactVO> dependencyList) throws Exception;
}
