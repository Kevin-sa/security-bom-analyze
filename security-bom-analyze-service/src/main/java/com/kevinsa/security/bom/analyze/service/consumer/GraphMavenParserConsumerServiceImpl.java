package com.kevinsa.security.bom.analyze.service.consumer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.kevinsa.security.bom.analyze.dao.graph.MvnGraphMapper;
import com.kevinsa.security.bom.analyze.enums.TaskStatusCode;
import com.kevinsa.security.bom.analyze.service.base.ScaParseServiceUnitTemplate;
import com.kevinsa.security.bom.analyze.service.common.BizCommonService;
import com.kevinsa.security.bom.analyze.service.dao.graph.impl.MvnGraphServiceImpl;
import com.kevinsa.security.bom.analyze.utils.EncryptUtils;
import com.kevinsa.security.bom.analyze.utils.ObjectMapperUtils;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.DependencyNodeVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.GraphEdgeVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.MessageGraphVO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GraphMavenParserConsumerServiceImpl extends ScaParseServiceUnitTemplate {

    private static final Logger logger = LoggerFactory.getLogger(GraphMavenParserConsumerServiceImpl.class);

    @Autowired
    private BizCommonService bizCommonService;

    @Autowired
    private EncryptUtils encryptUtils;

    @Autowired
    private MvnGraphServiceImpl mvnGraphService;

    @Autowired
    private MvnGraphMapper mvnGraphMapper;

    @Override
    protected Object paramCheck(String message) {
        return null;
    }

    @Override
    protected Object beforeBusiness(String message) {
        Assert.notNull(message, "GraphJarPomParserConsumerServiceImpl message is null");
        MessageGraphVO request = ObjectMapperUtils.fromJSON(message, MessageGraphVO.class);
        logger.debug("git remiteUrl:{}, model artifact:{}", request.getGitAddress(), request.getArtifactVO());
        bizCommonService.updateStatusCode(TaskStatusCode.TASK_DOING, encryptUtils.md5Encrypt(request.getGitAddress()), "consume");
        return request;
    }

    @Override
    protected Object doBusiness(Object beforeResult) {
        try {
            MessageGraphVO request = (MessageGraphVO) beforeResult;
            long vGitVid = mvnGraphService.VGitInsertIgnore(request.getGitAddress());
            long vArtifactVid = mvnGraphService.VModelInsertIgnore(request.getArtifactVO());
            GraphEdgeVO eDepMgmt = GraphEdgeVO.builder()
                    .sourceVid(vGitVid)
                    .sinkVid(vArtifactVid)
                    .build();
            mvnGraphMapper.EInsertDepMgmt(request.getBranch(), request.getCommitId(), eDepMgmt);

            // ????????????model??????dependencies???
            DependencyNodeVO dependency = request.getDependencyNodeVO();
            long vDepArtifactVid = mvnGraphService.VModelInsertIgnore(dependency.getArtifactVO());
            GraphEdgeVO eDeps = GraphEdgeVO.builder()
                    .sourceVid(vArtifactVid)
                    .sinkVid(vDepArtifactVid)
                    .build();
            mvnGraphMapper.EInsertDeps(request.getCommitId(), eDeps);
            parserDepList(vDepArtifactVid, dependency);
        } catch (Exception e) {
            logger.error("GraphMavenParserConsumer error:", e);
        }
        return null;
    }

    /**
     * ??????kafka????????????????????????model??????dependency???
     * @param sourceVid
     * @param dependencyNodeVO
     */
    protected void parserDepList(long sourceVid, DependencyNodeVO dependencyNodeVO) {
        // dependencyNodeVO ???????????????????????????artifactVO???????????????????????????insert model???
        long tempVid = mvnGraphService.VModelInsertIgnore(dependencyNodeVO.getArtifactVO());
        GraphEdgeVO eDep = GraphEdgeVO.builder()
                .sourceVid(sourceVid)
                .sinkVid(tempVid)
                .build();

        // ??????vid???????????????????????????????????????????????????sourceVid=sinkVid?????????????????????????????????
        if (eDep.getSinkVid() != eDep.getSourceVid()) {
            mvnGraphMapper.EInsertDep(dependencyNodeVO.getArtifactVO().getScope(), eDep);
        }

        // ??????children list???size?????????????????????????????????????????????
        if (dependencyNodeVO.getChildrenVO().size() != 0) {
            dependencyNodeVO.getChildrenVO().forEach(dependencyNodeVO1 -> {
                parserDepList(tempVid, dependencyNodeVO1);
            });
        }
    }

    @Override
    protected void afterBusiness(Object object) {

    }
}
