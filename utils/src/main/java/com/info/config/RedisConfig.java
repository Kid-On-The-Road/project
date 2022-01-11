package com.info.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import javax.annotation.Resource;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfig {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Bean
    public RedisTemplate<String, Object> redisCacheTemplate(LettuceConnectionFactory redisConnectionFactory) {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}