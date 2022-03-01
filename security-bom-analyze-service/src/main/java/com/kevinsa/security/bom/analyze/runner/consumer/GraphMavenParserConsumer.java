package com.kevinsa.security.bom.analyze.runner.consumer;

import com.kevinsa.security.bom.analyze.service.consumer.GraphMavenParserConsumerServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;



@Service
public class GraphMavenParserConsumer {

    @Autowired
    private GraphMavenParserConsumerServiceImpl GraphMavenParserConsumerServiceImpl;


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
        GraphMavenParserConsumerServiceImpl.execute(message);
    }


}
