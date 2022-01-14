package com.info.impl;

import com.info.dto.ShoppingCarQueryDto;
import com.info.entity.ShoppingCarEntity;
import com.info.mapper.GoodsEntityMapper;
import com.info.mapper.ShoppingCarEntityMapper;
import com.info.service.ShoppingService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public List<ShoppingCarQueryDto> selectSeckillGoodsList(Map<String ,Object> map) throws Exception {
        return shoppingCarEntityMapper.selectByCondition(map);
    }

    @Override
    public void saveSeckillRecord(Long userId, Long goodsId) throws ParseException {
        ShoppingCarEntity shoppingCarEntity = new ShoppingCarEntity();
        shoppingCarEntity.setUserId(userId);
        shoppingCarEntity.setGoodsId(goodsId);
        shoppingCarEntity.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(new Date())));
        shoppingCarEntity.setValidity("Y");
        shoppingCarEntity.setStatus("W");
        shoppingCarEntityMapper.insert(shoppingCarEntity);
    }

    @Override
    public void updateSeckillRecord(String type,String goodsId) {
        Map<String ,Object> map = new HashMap<>();
        map.put("type",type);
        map.put("goodsId",goodsId);
        shoppingCarEntityMapper.updateByGoodsId(map);
    }

    @Override
    public void deleteSeckillRecord(Long goodsId) {
        shoppingCarEntityMapper.deleteByPrimaryKey(goodsId);
    }
}
