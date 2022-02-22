package com.kevinsa.security.bom.analyze.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public enum SnowFlakeUtils {
    INSTANCE;

    private final SnowFlake snowFlake;

    private static final Logger logger = LoggerFactory.getLogger(SnowFlakeUtils.class);


    @Value("{snowFlake.datacenterId}")
    private long datacenterId;

    @Value("{snowFlake.machineId}")
    private long machineId;


    SnowFlakeUtils() {
        snowFlake = new SnowFlake(datacenterId, machineId);
    }

    public long getSnowFlakeId() {
        long id = snowFlake.nextId();
        logger.debug("vid:{}", id);
        return id;
    }
}
