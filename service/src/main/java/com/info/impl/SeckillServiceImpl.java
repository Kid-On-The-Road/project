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


    /**
     * 上架商品（redis中创建秒杀商品记录）
     *
     * @param goodsDto 商品对象
     */
    @Override
    public void addInventory(GoodsDto goodsDto) {
        redisTemplate.boundHashOps("秒杀商品").put(goodsDto.getGoodsId(), goodsDto);
    }

    /**
     * 扣减库存
     *
     * @param goodsId 商品ID
     * @return 返回商品数量
     */
    @Override
    public int deductionInventory(Long goodsId) {
        Map<String, Object> goodsDto = (Map<String, Object>) redisTemplate.boundHashOps("秒杀商品").get(goodsId);
        int goodsNumber = (int) Objects.requireNonNull(goodsDto).get("goodsNumber") - 1;
        goodsDto.put("goodsNumber", goodsNumber);
        redisTemplate.boundHashOps("秒杀商品").put(goodsId, goodsDto);
        return goodsNumber;
    }


    /**
     * 下架商品（redis中删除秒杀商品记录）
     *
     * @param goodsDto 商品对象
     */
    @Override
    public void deleteInventory(GoodsDto goodsDto) {
        redisTemplate.boundHashOps("秒杀商品").delete(goodsDto.getGoodsId());
    }

}
