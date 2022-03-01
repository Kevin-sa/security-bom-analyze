package com.kevinsa.security.bom.analyze.runner.consumer;

import com.kevinsa.security.bom.analyze.service.consumer.DetectMavenServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class DetectMavenParserConsumer {

    @Autowired
    private DetectMavenServiceImpl detectMavenService;

    /**
     * 用于依赖的异常检测，可以依赖于Snyk、NVD等漏洞数据库
     * 做漏洞artifact、groupId、version信息的判断
     * @param message
     */
    @KafkaListener(topics = "${kafka.topic.maven}", groupId = "${kafka.group.maven.detect}")
    public void consume(@Payload String message) {
        detectMavenService.execute(message);
    }
}
