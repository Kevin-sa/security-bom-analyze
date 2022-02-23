package com.kevinsa.security.bom.analyze.runner.consumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.kevinsa.security.bom.analyze.dao.graph.MvnGraphMapper;
import com.kevinsa.security.bom.analyze.service.dao.graph.impl.MvnGraphServiceImpl;
import com.kevinsa.security.bom.analyze.utils.ObjectMapperUtils;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.DependencyNodeVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.GraphEdgeVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.MessageGraphVO;


@Service
public class GraphMavenParserConsumer {

    private static final Logger logger = LoggerFactory.getLogger(GraphMavenParserConsumer.class);

    @Autowired
    private MvnGraphServiceImpl mvnGraphService;

    @Autowired
    private MvnGraphMapper mvnGraphMapper;


    /**
     * 如果写入失败是否直接抛出错误，同步状态码
     * 1、先判断git点是否存在，不存在写入，获取git点的vid
     * 2、判断artifactVO是否存在，不存在写入，获取artifactVO model点的vid
     * 3、childrenVO需要递归
     *
     * @param message
     */
    @KafkaListener(topics = "${kafka.topic.maven}", groupId = "${kafka.group.maven.graph}")
    public void consume(@Payload String message) {
        try {
            if (StringUtils.isBlank(message)) {
                logger.error("GraphMavenParserConsumer consumer error");
                return;
            }
            MessageGraphVO request = ObjectMapperUtils.fromJSON(message, MessageGraphVO.class);
            logger.debug("git remiteUrl:{}, model artifact:{}", request.getGitAddress(), request.getArtifactVO());
            // 用于构建git\model点及dependency_management边
            long vGitVid = mvnGraphService.VGitInsertIgnore(request.getGitAddress());
            long vArtifactVid = mvnGraphService.VModelInsertIgnore(request.getArtifactVO());
            GraphEdgeVO eDepMgmt = GraphEdgeVO.builder()
                    .sourceVid(vGitVid)
                    .sinkVid(vArtifactVid)
                    .build();
            mvnGraphMapper.EInsertDepMgmt(request.getBranch(), request.getCommitId(), eDepMgmt);

            // 用于构建model点及dependencies边
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

    }

    /**
     * 根据kafka中的消息循环构建model点、dependency边
     * @param sourceVid
     * @param dependencyNodeVO
     */
    protected void parserDepList(long sourceVid, DependencyNodeVO dependencyNodeVO) {
        // dependencyNodeVO 对象存在则一定存在artifactVO对象，此时应该优先insert model点
        long tempVid = mvnGraphService.VModelInsertIgnore(dependencyNodeVO.getArtifactVO());
        GraphEdgeVO eDep = GraphEdgeVO.builder()
                .sourceVid(sourceVid)
                .sinkVid(tempVid)
                .build();

        // 判断vid是否相等的原因是，第一次进入函数时sourceVid=sinkVid，避免出现自己指向自己
        if (eDep.getSinkVid() != eDep.getSourceVid()) {
            mvnGraphMapper.EInsertDep(dependencyNodeVO.getArtifactVO().getScope(), eDep);
        }

        // 如果children list的size为空，说明为最后一层，结束递归
        if (dependencyNodeVO.getChildrenVO().size() != 0) {
            dependencyNodeVO.getChildrenVO().forEach(dependencyNodeVO1 -> {
                parserDepList(tempVid, dependencyNodeVO1);
            });
        }
    }

}
