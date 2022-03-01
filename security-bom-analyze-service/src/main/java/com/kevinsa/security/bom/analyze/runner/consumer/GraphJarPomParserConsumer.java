package com.kevinsa.security.bom.analyze.runner.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.kevinsa.security.bom.analyze.service.consumer.GraphJarPomParserConsumerServiceImpl;


@Component
public class GraphJarPomParserConsumer {

    @Autowired
    private GraphJarPomParserConsumerServiceImpl graphJarPomParserConsumerService;


    /**
     * 构建jar包依赖的图
     *
     * @param message
     */
    @KafkaListener(topics = "${kafka.topic.jar-maven}", groupId = "${kafka.group.maven.graph}")
    public void consume(@Payload String message) {
        graphJarPomParserConsumerService.execute(message);
    }

}
