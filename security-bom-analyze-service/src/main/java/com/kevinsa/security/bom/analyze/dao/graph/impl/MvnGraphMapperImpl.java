package com.kevinsa.security.bom.analyze.dao.graph.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kevinsa.security.bom.analyze.constant.nebula.GraphClient;
import com.kevinsa.security.bom.analyze.dao.graph.MvnGraphMapper;
import com.kevinsa.security.bom.analyze.dao.graph.model.MvnGraphTemp;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.ArtifactVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.GraphEdgeVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.NebulaGraphRespVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.NebulaGraphSqlVO;
import com.vesoft.nebula.client.graph.data.ResultSet;


@Service
public class MvnGraphMapperImpl implements MvnGraphMapper {

    private static final Logger logger = LoggerFactory.getLogger(MvnGraphMapperImpl.class);

    @Autowired
    private MvnGraphTemp mvnGraphTemp;

    @Autowired
    private GraphClient graphClient;

    @Override
    public NebulaGraphRespVO VInsertGit(String gitAddress) throws Exception {
        NebulaGraphRespVO nebulaGraphRespVO = NebulaGraphRespVO.builder().build();
        NebulaGraphSqlVO nebulaGraphSqlVO = mvnGraphTemp.VInsertGitSql(gitAddress);
        ResultSet resp = graphClient.getInstance().execute(nebulaGraphSqlVO.getGraphSql());
        nebulaGraphRespVO.setResp(resp);
        nebulaGraphRespVO.setSucceeded(resp.isSucceeded());
        nebulaGraphRespVO.setVid(nebulaGraphSqlVO.getVid());
        return nebulaGraphRespVO;
    }

    @Override
    public NebulaGraphRespVO VInsertTModel(ArtifactVO artifactVO) throws Exception {
        NebulaGraphRespVO nebulaGraphRespVO = NebulaGraphRespVO.builder().build();
        NebulaGraphSqlVO nebulaGraphSqlVO = mvnGraphTemp.VInsertTModelSql(artifactVO);
        ResultSet resp = graphClient.getInstance().execute(nebulaGraphSqlVO.getGraphSql());
        nebulaGraphRespVO.setResp(resp);
        nebulaGraphRespVO.setSucceeded(resp.isSucceeded());
        nebulaGraphRespVO.setVid(nebulaGraphSqlVO.getVid());
        return nebulaGraphRespVO;
    }

    @Override
    public NebulaGraphRespVO VInsertTParentModel(ArtifactVO artifactVO) throws Exception {
        NebulaGraphRespVO nebulaGraphRespVO = NebulaGraphRespVO.builder().build();
        NebulaGraphSqlVO nebulaGraphSqlVO = mvnGraphTemp.VInsertTParentModelSql(artifactVO);
        ResultSet resp = graphClient.getInstance().execute(nebulaGraphSqlVO.getGraphSql());
        nebulaGraphRespVO.setResp(resp);
        nebulaGraphRespVO.setSucceeded(resp.isSucceeded());
        nebulaGraphRespVO.setVid(nebulaGraphSqlVO.getVid());
        return nebulaGraphRespVO;
    }

    @Override
    public NebulaGraphRespVO VInsertTJar(String packageName) throws Exception {
        NebulaGraphRespVO nebulaGraphRespVO = NebulaGraphRespVO.builder().build();
        NebulaGraphSqlVO nebulaGraphSqlVO = mvnGraphTemp.VInsertTJarSql(packageName);
        ResultSet resp = graphClient.getInstance().execute(nebulaGraphSqlVO.getGraphSql());
        nebulaGraphRespVO.setResp(resp);
        nebulaGraphRespVO.setSucceeded(resp.isSucceeded());
        nebulaGraphRespVO.setVid(nebulaGraphSqlVO.getVid());
        return nebulaGraphRespVO;
    }

    @Override
    public NebulaGraphRespVO EInsertDepMgmt(String branch, String commitId, GraphEdgeVO graphEdgeVO) throws Exception {
        NebulaGraphRespVO nebulaGraphRespVO = NebulaGraphRespVO.builder().build();
        String sql = mvnGraphTemp.EInsertDepMgmtSql(branch, commitId, graphEdgeVO);
        ResultSet resp = graphClient.getInstance().execute(sql);
        nebulaGraphRespVO.setResp(resp);
        nebulaGraphRespVO.setSucceeded(resp.isSucceeded());
        return nebulaGraphRespVO;
    }

    @Override
    public NebulaGraphRespVO EInsertDeps(String commitId, GraphEdgeVO graphEdgeVO) throws Exception {
        NebulaGraphRespVO nebulaGraphRespVO = NebulaGraphRespVO.builder().build();
        String sql = mvnGraphTemp.EInsertDepsSql(commitId, graphEdgeVO);
        ResultSet resp = graphClient.getInstance().execute(sql);
        nebulaGraphRespVO.setResp(resp);
        nebulaGraphRespVO.setSucceeded(resp.isSucceeded());
        return nebulaGraphRespVO;
    }

    @Override
    public NebulaGraphRespVO EInsertDep(String scope, GraphEdgeVO graphEdgeVO) {
        NebulaGraphRespVO nebulaGraphRespVO = NebulaGraphRespVO.builder().build();
        try {
            String sql = mvnGraphTemp.EInsertDepSql(scope, graphEdgeVO);
            ResultSet resp = graphClient.getInstance().execute(sql);
            nebulaGraphRespVO.setResp(resp);
            nebulaGraphRespVO.setSucceeded(resp.isSucceeded());
        } catch (Exception e) {
            logger.error("VInsertGit error", e);
        }
        return nebulaGraphRespVO;
    }

    @Override
    public NebulaGraphRespVO EInsertParent(GraphEdgeVO graphEdgeVO) {
        NebulaGraphRespVO nebulaGraphRespVO = NebulaGraphRespVO.builder().build();
        try {
            String sql = mvnGraphTemp.EInsertParentSql(graphEdgeVO);
            ResultSet resp = graphClient.getInstance().execute(sql);
            nebulaGraphRespVO.setResp(resp);
            nebulaGraphRespVO.setSucceeded(resp.isSucceeded());
        } catch (Exception e) {
            logger.error("VInsertGit error", e);
        }
        return nebulaGraphRespVO;
    }

    @Override
    public ResultSet VLookUpGit(String gitAddress) throws Exception {
        String sql = mvnGraphTemp.VLookUpGitSql(gitAddress);
        return graphClient.getInstance().execute(sql);
    }

    @Override
    public ResultSet VLookUpModel(ArtifactVO artifactVO) throws Exception {
        String sql = mvnGraphTemp.VLookUpModelSql(artifactVO);
        return graphClient.getInstance().execute(sql);
    }

    @Override
    public ResultSet VLookUpParentModel(ArtifactVO artifactVO) throws Exception {
        String sql = mvnGraphTemp.VLookUpParentModelSql(artifactVO);
        return graphClient.getInstance().execute(sql);
    }

    @Override
    public ResultSet VLookUpJar(String packageName) throws Exception {
        String sql = mvnGraphTemp.VLookUpJarSql(packageName);
        return graphClient.getInstance().execute(sql);
    }
}