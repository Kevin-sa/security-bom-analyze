package com.kevinsa.security.bom.analyze.service.common;

public interface KafkaCommonService {
    void sendMsg(String topic, String msg);
}
