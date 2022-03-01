package com.kevinsa.security.bom.analyze.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Object get(String key) {
        Assert.notNull(key, "RedisUtils get key is null");
        return redisTemplate.opsForValue().get(key);
    }

    public void set(String key, Object value) {
        Assert.notNull(key, "RedisUtils set key is null");
        Assert.notNull(value, "RedisUtils set value is null");
        redisTemplate.opsForValue().set(key, value);
    }

    public void expire(String key, long time) {
        Assert.notNull(key, "RedisUtils expire key is null");
        Assert.notNull(key, "RedisUtils expire time is null");
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }
}
