package com.info.impl;

import com.info.convert.ConvertUtil;
import com.info.dto.GoodsDto;
import com.info.entity.GoodsEntity;
import com.info.mapper.GoodsEntityMapper;
import com.info.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private GoodsEntityMapper goodsEntityMapper;
    @Override
    public int save(GoodsDto goodsDto) throws Exception {
        GoodsEntity goodsEntity = ConvertUtil.convert(goodsDto, GoodsEntity.class);
        if(goodsEntity.getGoodsId()>0){
            return goodsEntityMapper.updateById(goodsEntity);
        }
        return goodsEntityMapper.insert(goodsEntity);
    }

    @Override
    public GoodsDto selectByGoodsId(Long goodsId) throws Exception {
        GoodsEntity goodsEntity = goodsEntityMapper.selectByPrimaryKey(goodsId);
        return ConvertUtil.convert(goodsEntity, GoodsDto.class);
    }

    @Override
    public List<GoodsDto> selectByCondition(Map<String, Object> map,int pageNum) throws Exception {

        List<GoodsEntity> goodsEntities = goodsEntityMapper.selectByCondition(map);
        List<GoodsDto> goodsDtos = new ArrayList<>();
        for (GoodsEntity goodsEntity : goodsEntities) {
            GoodsDto goodsDto = ConvertUtil.convert(goodsEntity, GoodsDto.class);
            goodsDtos.add(goodsDto);
        }
        return goodsDtos;
    }

    @Override
    public int deleteByGoodsId(Long goodsId) {
        return goodsEntityMapper.deleteByPrimaryKey(goodsId);
    }

    @Override
    public void updateStatus(GoodsDto goodsDto) {
        goodsEntityMapper.updateStatus(goodsDto);
    }

}
