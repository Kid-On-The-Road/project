package com.info.impl;

import com.info.Excel.ExcelUpload;
import com.info.convert.ConvertUtil;
import com.info.dto.GoodsDto;
import com.info.entity.GoodsEntity;
import com.info.mapper.GoodsEntityMapper;
import com.info.service.GoodsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private GoodsEntityMapper goodsEntityMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public int save(GoodsDto goodsDto) throws Exception {
        if (redisTemplate.boundHashOps("上架商品").get(goodsDto.getGoodsId()) != null) {
            redisTemplate.boundHashOps("上架商品").put(
                    goodsDto.getGoodsId(),
                    (int) redisTemplate.boundHashOps("上架商品").get(goodsDto.getGoodsId()) - (goodsEntityMapper.selectByPrimaryKey(goodsDto.getGoodsId()).getGoodsNumber() - goodsDto.getGoodsNumber()));
        }
        GoodsEntity goodsEntity = ConvertUtil.convert(goodsDto, GoodsEntity.class);
        if (goodsEntity.getGoodsId() > 0) {
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
    public List<GoodsDto> selectByCondition(Map<String, Object> map, int pageNum) throws Exception {

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
        goodsDto.setGoodsNumber((int) redisTemplate.boundHashOps("上架商品").get(goodsDto.getGoodsId()));
        goodsEntityMapper.updateStatus(goodsDto);
    }

    @Override
    public int uploadGoods(MultipartFile file) throws Exception {
        Map<Object, List<Object>> goodsListMap = ExcelUpload.loadData(file, 1, 2);
        List<String> headerList = ExcelUpload.getHeaderList();
        for (Object o : goodsListMap.keySet()) {
            List<Object> goodsInfos = goodsListMap.get(o);
            GoodsEntity goodsEntity = ConvertUtil.listToObject(goodsInfos, GoodsEntity.class, headerList);
            goodsEntity.setValidity("Y");
            goodsEntity.setStatus("S");
            return goodsEntityMapper.insert(goodsEntity);
        }
        return 0;
    }
}
