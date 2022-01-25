package com.info.impl;

import com.info.dto.GoodsDto;
import com.info.service.OrderService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private AmqpTemplate amqpTemplate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 上架商品（redis中创建订单记录）
     *
     * @param goodsDto 商品对象
     */
    @Override
    public void addInventory(GoodsDto goodsDto) {
        redisTemplate.boundHashOps("上架商品").put(goodsDto.getGoodsId(), goodsDto);
    }

    /**
     * 扣减库存
     *
     * @param goodsId 商品ID
     * @return 返回商品数量
     */
    @Override
    public int deductionInventory(Long goodsId) {
        Map<String, Object> goodsDto = (Map<String, Object>) redisTemplate.boundHashOps("上架商品").get(goodsId);
        int goodsNumber = (int) Objects.requireNonNull(goodsDto).get("goodsNumber") - 1;
        goodsDto.put("goodsNumber", goodsNumber);
        redisTemplate.boundHashOps("上架商品").put(goodsId, goodsDto);
        return goodsNumber;
    }


    /**
     * 下架商品（redis中删除订单记录）
     *
     * @param goodsDto 商品对象
     */
    @Override
    public void deleteInventory(GoodsDto goodsDto) {
        redisTemplate.boundHashOps("上架商品").delete(goodsDto.getGoodsId());
    }

    /**
     * 保存用户信息
     *
     * @param goodsId
     * @param userId
     */
    @Override
    public void saveUserInfo(Long goodsId, Long userId) {
        redisTemplate.boundHashOps("用户信息").put(goodsId+userId, userId);
    }

    /**
     * 删除用户信息
     *
     * @param goodsId
     */
    @Override
    public void deleteUserInfo(Long goodsId) {
        redisTemplate.boundHashOps("用户信息").delete(goodsId);
    }

}
