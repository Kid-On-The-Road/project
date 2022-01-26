package com.info.impl;

import com.info.convert.ConvertUtil;
import com.info.dto.GoodsDto;
import com.info.entity.GoodsEntity;
import com.info.mapper.GoodsEntityMapper;
import com.info.service.GoodsService;
import com.info.service.OrderService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private AmqpTemplate amqpTemplate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GoodsEntityMapper goodsEntityMapper;
    @Resource
    private GoodsService goodsService;

    /**
     * 添加商品（redis中添加商品信息）
     *
     * @param goodsDto 商品对象
     */
    @Override
    public void addInventory(GoodsDto goodsDto) {
        redisTemplate.boundHashOps("上架商品").put(goodsDto.getGoodsId(), goodsDto.getGoodsNumber());
    }

    /**
     * 扣减库存
     *
     * @param goodsId 商品ID
     * @return 返回商品数量
     */
    @Override
    public int deductionInventory(Long goodsId) {
        int goodsNumber = (int) redisTemplate.boundHashOps("上架商品").get(goodsId) - 1;
        redisTemplate.boundHashOps("上架商品").put(goodsId, goodsNumber);
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
    public int saveUserInfo(Long goodsId, Long userId, int orderNumber) {
        Object o = redisTemplate.boundHashOps("用户信息").get(goodsId + userId);
        if (redisTemplate.boundHashOps("用户信息").get(goodsId + userId) != null) {
            return 2;
        } else if ((Integer) redisTemplate.boundHashOps("上架商品").get(goodsId) > 0) {
            Map<String, Object> userInfo = new HashMap<String, Object>();
            userInfo.put("userId", userId);
            userInfo.put("goodsId", goodsId);
            userInfo.put("orderNumber", orderNumber);
            userInfo.put("status", "W");
            redisTemplate.boundHashOps("用户信息").put(goodsId + userId, userInfo);
            redisTemplate.boundHashOps("上架商品").put(goodsId, (int) redisTemplate.boundHashOps("上架商品").get(goodsId) - orderNumber);
            //设置过期时间
            redisTemplate.expire(String.valueOf(goodsId + userId), 10, TimeUnit.SECONDS);
            return 1;
        }
        return 0;
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

    /**
     * 根据条件查询商品
     */
    @Override
    public List<GoodsDto> selectByCondition(Map<String, Object> map, int pageNum) throws Exception {
        List<GoodsEntity> goodsEntityList = goodsEntityMapper.selectByCondition(map);
        List<GoodsDto> goodsDtos = new ArrayList<>();
        for (GoodsEntity goodsEntity : goodsEntityList) {
            goodsEntity.setGoodsNumber((int) redisTemplate.boundHashOps("上架商品").get(goodsEntity.getGoodsId()));
            GoodsDto goodsDto = ConvertUtil.convert(goodsEntity, GoodsDto.class);
            goodsDtos.add(goodsDto);
        }

        return goodsDtos;
    }

    /**
     * 根据ID查询商品
     */
    @Override
    public GoodsDto selectByGoodsId(Long goodsId) throws Exception {
        GoodsEntity goodsEntity = goodsEntityMapper.selectByPrimaryKey(goodsId);
        return ConvertUtil.convert(goodsEntity, GoodsDto.class);
    }
}
