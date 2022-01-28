package com.info.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RedisUtil {
    @Resource
    private RedisTemplate redisTemplate;
    public  <T> T getAndSet(final String key, T value) {
        T oldValue = null;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            oldValue =(T) operations.getAndSet(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldValue;
    }
}
