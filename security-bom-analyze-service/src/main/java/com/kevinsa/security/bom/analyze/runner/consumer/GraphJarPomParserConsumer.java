package com.kevinsa.security.bom.analyze.runner.consumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.kevinsa.security.bom.analyze.dao.graph.MvnGraphMapper;
import com.kevinsa.security.bom.analyze.service.dao.graph.impl.MvnGraphServiceImpl;
import com.kevinsa.security.bom.analyze.utils.ObjectMapperUtils;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.GraphEdgeVO;
import com.kevinsa.security.bom.analyze.vo.mvnPlugin.JarMavenVO;


@Component
public class GraphJarPomParserConsumer {

    private static final Logger logger = LoggerFactory.getLogger(GraphJarPomParserConsumer.class);

    @Autowired
    private MvnGraphServiceImpl mvnGraphService;

    @Autowired
    private MvnGraphMapper mvnGraphMapper;


    /**
     * 构建jar包依赖的图
     *
     * @param message
     */
    @KafkaListener(topics = "${kafka.topic.jar-maven}", groupId = "${kafka.group.maven.graph}")
    public void consume(@Payload String message) {
        try {
            if (StringUtils.isBlank(message)) {
                logger.error("GraphMavenParserConsumer consumer error");
                return;
            }
            JarMavenVO request = ObjectMapperUtils.fromJSON(message, JarMavenVO.class);
            logger.debug("consume jar name:{}", request.getPackageName());
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

        } catch (Exception e) {
            logger.error("consume error:", e);
        }
    }

}
