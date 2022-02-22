package com.kevinsa.security.bom.analyze.service.dao.graph.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kevinsa.security.bom.analyze.dao.graph.MvnGraphMapper;
import com.kevinsa.security.bom.analyze.service.dao.graph.MvnGraphService;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.ArtifactVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.NebulaGraphRespVO;
import com.vesoft.nebula.client.graph.data.ResultSet;


@Service
public class MvnGraphServiceImpl implements MvnGraphService {

    private static final Logger logger = LoggerFactory.getLogger(MvnGraphServiceImpl.class);

    @Autowired
    private MvnGraphMapper mvnGraphMapper;

    /**
     * 如果查询失败或者查询结果为空则写入
     *
     * @param gitAddress
     * @return
     */
    @Override
    public long VGitInsertIgnore(String gitAddress) {
        long vid = 0;
        try {
            ResultSet resp = mvnGraphMapper.VLookUpGit(gitAddress);
            if (!resp.isSucceeded() || resp.getRows().size() == 0) {
                NebulaGraphRespVO nebulaGraphRespVO = mvnGraphMapper.VInsertGit(gitAddress);
                return nebulaGraphRespVO.getVid();
            } else {
                return resp.getRows().get(0).values.get(0).getIVal();
            }
        } catch (Exception e) {
            logger.error("VGitInsertIgnore error", e);
        }
        return vid;
    }

    @Override
    public long VModelInsertIgnore(ArtifactVO artifactVO) {
        long vid = 0;
        try {
            ResultSet resp = mvnGraphMapper.VLookUpModel(artifactVO);
            if (!resp.isSucceeded() || resp.getRows().size() == 0) {
                NebulaGraphRespVO nebulaGraphRespVO = mvnGraphMapper.VInsertTModel(artifactVO);
                return nebulaGraphRespVO.getVid();
            } else {
                return resp.getRows().get(0).values.get(0).getIVal();
            }
        } catch (Exception e) {
            logger.error("VModelInsertIgnore error", e);
        }
        return vid;
    }

    @Override
    public long VParentModelInsertIgnore(ArtifactVO artifactVO) {
        long vid = 0;
        try {
            ResultSet resp = mvnGraphMapper.VLookUpParentModel(artifactVO);
            if (!resp.isSucceeded() || resp.getRows().size() == 0) {
                NebulaGraphRespVO nebulaGraphRespVO = mvnGraphMapper.VInsertTParentModel(artifactVO);
                return nebulaGraphRespVO.getVid();
            } else {
                return resp.getRows().get(0).values.get(0).getIVal();
            }
        } catch (Exception e) {
            logger.error("VModelInsertIgnore error", e);
        }
        return vid;
    }

    @Override
    public long VJarInsertIgnore(String packageName) {
        long vid = 0;
        try {
            ResultSet resp = mvnGraphMapper.VLookUpJar(packageName);
            if (!resp.isSucceeded() || resp.getRows().size() == 0) {
                NebulaGraphRespVO nebulaGraphRespVO = mvnGraphMapper.VInsertTJar(packageName);
                return nebulaGraphRespVO.getVid();
            } else {
                return resp.getRows().get(0).values.get(0).getIVal();
            }
        } catch (Exception e) {
            logger.error("VJarInsertIgnore error", e);
        }
        return vid;
    }
}
