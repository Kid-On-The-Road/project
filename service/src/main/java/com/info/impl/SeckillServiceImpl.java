package com.info.impl;

import com.info.dto.GoodsDto;
import com.info.service.SeckillService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

@Service
public class SeckillServiceImpl implements SeckillService {
    @Resource
    private AmqpTemplate amqpTemplate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void addInventory(GoodsDto goodsDto) {
            redisTemplate.boundHashOps("秒杀商品").put(goodsDto.getGoodsId(), goodsDto);
    }

    @Override
    public void updateSeckillData(GoodsDto goodsDto) {
//        redisTemplate.opsForValue().set(i+"Id", i+"Id")
    }

    @Override
    public int deductionInventory(long goodsId) {
        Map<String,Object> goodsDto = (Map<String, Object>) redisTemplate.boundHashOps("秒杀商品").get(goodsId);
        int goodsNumber = (int) Objects.requireNonNull(goodsDto).get("goodsNumber") - 1;
        goodsDto.put("goodsNumber", goodsNumber);
        redisTemplate.boundHashOps("秒杀商品").put(goodsId,goodsDto);
        return goodsNumber;
    }

    @Override
    public void deleteInventory(GoodsDto goodsDto) {
        redisTemplate.boundHashOps("秒杀商品").delete(goodsDto.getGoodsName());
    }
}
