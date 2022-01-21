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
    public int saveSeckillRecord(Map map) throws ParseException {
        if (map.size() > 0) {
            List<ShoppingCarEntity> shoppingCarEntities = new ArrayList<>();
            for (Object o : map.keySet()) {
                ShoppingCarEntity shoppingCarEntity = new ShoppingCarEntity();
                shoppingCarEntity.setUserId(121L);
                shoppingCarEntity.setGoodsId(242L);
                shoppingCarEntity.setCreateTime(new Date());
                shoppingCarEntity.setValidity("Y");
                shoppingCarEntity.setStatus("W");
                shoppingCarEntities.add(shoppingCarEntity);
            }
            return shoppingCarEntityMapper.insertBatchWithAll(shoppingCarEntities);
        }
        return 0;
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
    public void deleteSeckillRecord(Long goodsId, Long userId) throws Exception {
        //恢复缓存中商品的库存
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("goodsId", goodsId);
        for (ShoppingCarQueryDto shoppingCarQueryDto : shoppingCarEntityMapper.selectByCondition(map)) {
            if (!shoppingCarQueryDto.getStatus().equals("P")) {
                redisTemplate.boundHashOps("秒杀商品").put(goodsId, ConvertUtil.convert(goodsEntityMapper.selectByPrimaryKey(goodsId), GoodsDto.class));
            }
        }
        //删除订单信息
        shoppingCarEntityMapper.deleteByPrimaryKey(goodsId);
        redisTemplate.boundHashOps("用户信息").delete(goodsId + userId);
    }
}
