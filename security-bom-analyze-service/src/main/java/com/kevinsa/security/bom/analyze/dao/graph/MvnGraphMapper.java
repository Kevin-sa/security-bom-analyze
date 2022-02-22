package com.kevinsa.security.bom.analyze.dao.graph;

import com.kevinsa.security.bom.analyze.vo.mvnPlugin.ArtifactVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.GraphEdgeVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.NebulaGraphRespVO;
import com.vesoft.nebula.client.graph.data.ResultSet;

public interface MvnGraphMapper {
    NebulaGraphRespVO VInsertGit(String gitAddress) throws Exception;

    NebulaGraphRespVO VInsertTModel(ArtifactVO artifactVO) throws Exception;

    NebulaGraphRespVO VInsertTParentModel(ArtifactVO artifactVO) throws Exception;

    NebulaGraphRespVO VInsertTJar(String packageName) throws Exception;

    NebulaGraphRespVO EInsertDepMgmt(String branch, String commitId, GraphEdgeVO graphEdgeVO) throws Exception;

    NebulaGraphRespVO EInsertDeps(String commitId, GraphEdgeVO graphEdgeVO) throws Exception;

    NebulaGraphRespVO EInsertDep(String scope, GraphEdgeVO graphEdgeVO);

    NebulaGraphRespVO EInsertParent(GraphEdgeVO graphEdgeVO);

    ResultSet VLookUpGit(String gitAddress) throws Exception;

    ResultSet VLookUpModel(ArtifactVO artifactVO) throws Exception;

    ResultSet VLookUpParentModel(ArtifactVO artifactVO) throws Exception;

    ResultSet VLookUpJar(String packageName) throws Exception;
}
