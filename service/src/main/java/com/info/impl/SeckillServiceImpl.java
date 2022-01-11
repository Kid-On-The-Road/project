package com.info.impl;

import com.info.dto.GoodsDto;
import com.info.service.SeckillService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SeckillServiceImpl implements SeckillService {
    @Resource
    private AmqpTemplate amqpTemplate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void addInventory(GoodsDto goodsDto) {
        for (int i = 0; i < goodsDto.getGoodsNumber(); i++) {
            redisTemplate.boundHashOps(goodsDto.getGoodsName()).put(goodsDto.getGoodsId(),goodsDto);
        }
    }

    @Override
    public void updateSeckillData(GoodsDto goodsDto) {
//        redisTemplate.opsForValue().set(i+"Id", i+"Id")
    }

    @Override
    public void deductionInventory(GoodsDto goodsDto) {

    }

    @Override
    public void deleteInventory(GoodsDto goodsDto) {
        redisTemplate.delete(goodsDto.getGoodsName());
    }
}
