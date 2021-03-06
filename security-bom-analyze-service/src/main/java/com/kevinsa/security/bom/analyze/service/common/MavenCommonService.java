package com.kevinsa.security.bom.analyze.service.common;

import org.apache.maven.model.Model;

import com.kevinsa.security.bom.analyze.vo.mvnPlugin.ArtifactVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.JarMavenVO;

import java.io.File;
import java.util.List;

public interface MavenCommonService {
    /**
     * todo: version版本号在properties中获取的方式
     * @return
     * @throws Exception
     */
    Model getPomInfo(File filePath) throws Exception;

    JarMavenVO getJarMvnInfoByPom(File file, String type, List<ArtifactVO> dependencyList) throws Exception;
}
