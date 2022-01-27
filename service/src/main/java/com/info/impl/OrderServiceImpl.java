package com.info.impl;

import com.info.convert.ConvertUtil;
import com.info.dto.GoodsDto;
import com.info.entity.GoodsEntity;
import com.info.mapper.GoodsEntityMapper;
import com.info.service.OrderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GoodsEntityMapper goodsEntityMapper;

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
        if (redisTemplate.boundHashOps("用户信息").get(goodsId + userId) != null) {
            return 2;
        } else if ((Integer) redisTemplate.boundHashOps("上架商品").get(goodsId)>=orderNumber) {
            Map<String, Object> userInfo = new HashMap<String, Object>();
            userInfo.put("userId", userId);
            userInfo.put("goodsId", goodsId);
            userInfo.put("orderNumber", orderNumber);
            userInfo.put("status", "W");
            redisTemplate.boundHashOps("用户信息").put(goodsId + userId, userInfo);
            redisTemplate.boundHashOps("上架商品").put(goodsId, (int) redisTemplate.boundHashOps("上架商品").get(goodsId) - orderNumber);
            //设置过期时间
            redisTemplate.boundValueOps(String.valueOf(goodsId+userId)).set(userId,10, TimeUnit.SECONDS);
            return 1;
        }
        return 0;
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
