package com.kevinsa.security.bom.maven.plugin;

import com.kevinsa.security.bom.maven.plugin.parser.vo.ArtifactVO;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PluginTest {
    @Test
    public void AnalyzePomInfoPOTest() {
        ArtifactVO artifactVO = ArtifactVO.builder().build();
        System.out.println(artifactVO.getType());
    }

    /**
     * 发送消息的方法使用future.get同步调用，plugin场景下可以接受Kafka发送的linger
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void KafkaSendTest() throws ExecutionException, InterruptedException {
        //1.加载配置信息
        Properties prop = loadProperties();

        //2.创建生产者
        KafkaProducer<String,String> producer = new KafkaProducer<>(prop);

        String sendContent = "hello_kafka";
        ProducerRecord<String,String> record = new ProducerRecord<>("security_sca_pom",sendContent);

        Future<RecordMetadata> future = producer.send(record);

        RecordMetadata recordMetadata = future.get();
        System.out.println(recordMetadata.offset());

    }

    /**
     * Kafka基本配置
     * @return
     */
    protected static Properties loadProperties() {
        Properties prop = new Properties();
        prop.put("bootstrap.servers", "127.0.0.1:9092");
        prop.put("retries", 0);
        prop.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        prop.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return prop;
    }

    @Test
    public void JsonTest() {
        ArtifactVO artifactVO = ArtifactVO.builder()
                .version("1.0.0.1")
                .build();
        JSONObject json = new JSONObject(artifactVO);
        System.out.println(json.toString());
    }


}
