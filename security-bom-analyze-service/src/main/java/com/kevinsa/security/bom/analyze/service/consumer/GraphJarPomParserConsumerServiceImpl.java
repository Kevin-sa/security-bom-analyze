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
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.GraphEdgeVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.JarMavenVO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GraphJarPomParserConsumerServiceImpl extends ScaParseServiceUnitTemplate {

    @Autowired
    private MvnGraphServiceImpl mvnGraphService;

    @Autowired
    private MvnGraphMapper mvnGraphMapper;

    @Autowired
    private BizCommonService bizCommonService;

    @Autowired
    private EncryptUtils encryptUtils;

    private static final Logger logger = LoggerFactory.getLogger(GraphJarPomParserConsumerServiceImpl.class);


    @Override
    protected Object paramCheck(String message) {
        return null;
    }

    @Override
    protected Object beforeBusiness(String message) {
        Assert.notNull(message, "GraphJarPomParserConsumerServiceImpl message is null");
        JarMavenVO request = ObjectMapperUtils.fromJSON(message, JarMavenVO.class);
        logger.debug("consume jar name:{}", request.getPackageName());
        bizCommonService.updateStatusCode(TaskStatusCode.TASK_DOING, encryptUtils.md5Encrypt(request.getPackageName()), "consume");
        return request;
    }

    @Override
    protected Object doBusiness(Object beforeResult) {
        JarMavenVO request = (JarMavenVO) beforeResult;
        // 先构建Jar点
        long vJarVid = mvnGraphService.VJarInsertIgnore(request.getPackageName());

        if (request.getParent() != null) {
            long vArtifactVid = mvnGraphService.VParentModelInsertIgnore(request.getParent());

            GraphEdgeVO eParent = GraphEdgeVO.builder()
                    .sourceVid(vJarVid)
                    .sinkVid(vArtifactVid)
                    .build();
            mvnGraphMapper.EInsertParent(eParent);
        }

        // 构建parent边
        request.getDependencies().forEach(item -> {
            long itemVid = mvnGraphService.VModelInsertIgnore(item);
            GraphEdgeVO eDep = GraphEdgeVO.builder()
                    .sourceVid(vJarVid)
                    .sinkVid(itemVid)
                    .build();
            mvnGraphMapper.EInsertDep(item.getScope(), eDep);
        });
        return null;
    }

    @Override
    protected void afterBusiness(Object object) {

    }
}
