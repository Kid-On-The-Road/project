package com.info.impl;

import com.info.convert.ConvertUtil;
import com.info.dto.GoodsDto;
import com.info.dto.ShoppingCarQueryDto;
import com.info.entity.GoodsEntity;
import com.info.entity.ShoppingCarEntity;
import com.info.mapper.GoodsEntityMapper;
import com.info.mapper.ShoppingCarEntityMapper;
import com.info.service.ShoppingService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

@Service
public class ShoppingServiceImpl implements ShoppingService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GoodsServiceImpl goodsService;
    @Resource
    private GoodsEntityMapper goodsEntityMapper;
    @Resource
    private ShoppingCarEntityMapper shoppingCarEntityMapper;

    @Override
    public List<ShoppingCarQueryDto> selectSeckillGoodsList(Map<String, Object> map) throws Exception {
        return shoppingCarEntityMapper.selectByCondition(map);
    }

    @Override
    public void saveSeckillRecord(Long userId, Long goodsId) throws ParseException {
        ShoppingCarEntity shoppingCarEntity = new ShoppingCarEntity();
        shoppingCarEntity.setUserId(userId);
        shoppingCarEntity.setGoodsId(goodsId);
        shoppingCarEntity.setCreateTime(new Date());
        shoppingCarEntity.setValidity("Y");
        shoppingCarEntity.setStatus("W");
        shoppingCarEntityMapper.insert(shoppingCarEntity);
    }

    @Override
    public void updateSeckillRecord(String type, String goodsId) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("goodsId", goodsId);
        shoppingCarEntityMapper.updateByGoodsId(map);
        goodsEntityMapper.deductionInventory(type, goodsId);
    }

    @Override
    public void deleteSeckillRecord(Long goodsId,Long userId) throws Exception {
        //删除订单信息
        shoppingCarEntityMapper.deleteByPrimaryKey(goodsId);
        //恢复缓存中商品的库存
        Map<String,Object> goodsDto = (Map<String, Object>) redisTemplate.boundHashOps("秒杀商品").get(goodsId);
        int goodsNumber = (int) Objects.requireNonNull(goodsDto).get("goodsNumber");
        if (goodsNumber <= 0) {
            goodsDto.put("goodsNumber", goodsNumber + goodsEntityMapper.selectByPrimaryKey(goodsId).getGoodsNumber());
            Map<String,Object> map = new HashMap<>();
            map.put("goodsId",goodsId);
            map.put("status","P");
            for (GoodsEntity goodsEntity : goodsEntityMapper.selectByCondition(map)) {
                goodsEntity.setStatus("P");
                goodsEntityMapper.updateStatus(ConvertUtil.convert(goodsEntity, GoodsDto.class));
            }
        }else {
            goodsDto.put("goodsNumber", goodsNumber + goodsEntityMapper.selectByPrimaryKey(goodsId).getGoodsNumber());
        }
        redisTemplate.boundHashOps("秒杀商品").put(goodsId,goodsDto);
        //删除缓存中的订单信息
//        redisTemplate.boundListOps(String.valueOf(userId)).remove(1,goodsId);
//        redisTemplate.boundListOps(userId+"").remove(userId,goodsId);
    }
}