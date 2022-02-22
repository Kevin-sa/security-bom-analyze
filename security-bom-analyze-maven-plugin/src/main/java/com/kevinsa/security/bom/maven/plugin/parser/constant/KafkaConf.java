package com.kevinsa.security.bom.maven.plugin.parser.constant;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.maven.plugin.MojoFailureException;

import java.util.Properties;

public class KafkaConf {

    private static final String bootStrapServers = "127.0.0.1:9092";
    private static final String serializer = "org.apache.kafka.common.serialization.StringSerializer";

    public KafkaProducer<String, String> getKafkaInstance() throws MojoFailureException {
        try {
            Properties prop = loadProperties();
            return new KafkaProducer<>(prop);
        } catch (Exception e) {
            throw new MojoFailureException("getKafkaInstance", e);
        }
    }

    protected static String bootStrapServers() {
        return bootStrapServers;
    }

    protected static Properties loadProperties() {
        Properties prop = new Properties();
        prop.put("bootstrap.servers", bootStrapServers());
        prop.put("retries", 0);
        prop.put("key.serializer", serializer);
        prop.put("value.serializer", serializer);
        return prop;
    }
}
