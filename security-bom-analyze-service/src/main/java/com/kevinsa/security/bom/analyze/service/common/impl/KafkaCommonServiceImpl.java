package com.kevinsa.security.bom.analyze.service.common.impl;

import com.kevinsa.security.bom.analyze.service.common.KafkaCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaCommonServiceImpl implements KafkaCommonService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(KafkaCommonServiceImpl.class);


    @Override
    public void sendMsg(String topic, String msg) {
        kafkaTemplate.send(topic, msg)
                .addCallback(result -> {
                    assert result != null;
                    if (null != result.getRecordMetadata()) {
                        logger.info("消费发送成功 offset:{}", result.getRecordMetadata().offset());
                        return;
                    }
                    logger.info("消息发送成功");
                }, throwable -> logger.error("消费发送失败 error:{}", throwable.getMessage()));
    }
}
