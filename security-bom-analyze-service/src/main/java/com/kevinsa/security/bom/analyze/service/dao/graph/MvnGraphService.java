package com.kevinsa.security.bom.analyze.service.dao.graph;

import com.kevinsa.security.bom.analyze.vo.mvnPlugin.ArtifactVO;

public interface MvnGraphService {
    long VGitInsertIgnore(String gitAddress);

    long VModelInsertIgnore(ArtifactVO artifactVO);

    long VParentModelInsertIgnore(ArtifactVO artifactVO);

    long VJarInsertIgnore(String packageName);
}
