package com.info.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.info.dto.GoodsDto;
import com.info.impl.GoodsServiceImpl;
import com.info.service.OrderService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
public class GoodsManageController {
    @Resource
    private GoodsServiceImpl goodsService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private OrderService seckillService;
    @Resource
    private AmqpTemplate amqpTemplate;

    /**
     * 保存/修改商品信息
     *
     * @param goodsDto 需要保存的商品对象
     */
    @RequestMapping(value = "saveGoods", method = RequestMethod.POST)
    @ResponseBody
    public int save(@ModelAttribute(value = "save") GoodsDto goodsDto) throws Exception {
        return goodsService.save(goodsDto);
    }

    /**
     * 删除商品
     *
     * @param goodsId 商品ID
     */
    @GetMapping("deleteGoods")
    @ResponseBody
    public int delete(@RequestParam(required = false, value = "goodsId") Long goodsId) {
        return goodsService.deleteByGoodsId(goodsId);
    }

    /**
     * 根据ID查询商品
     *
     * @param goodsId 商品ID
     */
    @RequestMapping(value = "selectGoods", method = RequestMethod.POST)
    @ResponseBody
    public GoodsDto selectGoods(
            @RequestParam(required = false, value = "goodsId") Long goodsId) throws Exception {
        return goodsService.selectByGoodsId(goodsId);
    }

    /**
     * 根据条件查询商品
     *
     * @param goodsName      商品名称
     * @param goodsCategory  商品类型
     * @param productionTime 生产日期
     * @param pageNum        当前页
     * @param pageSize       每页显示的数据条数
     */
    @RequestMapping(value = "selectGoodsList")
    public ModelAndView selectGoodsList(
            @RequestParam(required = false, value = "goodsName") String goodsName,
            @RequestParam(required = false, value = "goodsCategory") String goodsCategory,
            @RequestParam(required = false, value = "productionTime") String productionTime,
            @RequestParam(required = false, value = "seckillStatus") String status,
            @RequestParam(required = false, value = "userId") String userId,
            @RequestParam(required = false, defaultValue = "1", value = "pageNum") int pageNum,
            @RequestParam(required = false, defaultValue = "10", value = "pageSize") int pageSize, ModelAndView mv) throws Exception {
        if (!Objects.equals(userId, "") && !Objects.equals(userId, null)) {
            Page<GoodsDto> page = PageHelper.startPage(pageNum, pageSize);
            Map<String, Object> map = new HashMap<>();
            if (!Objects.isNull(goodsName) && goodsName.length() > 0) {
                map.put("goodsName", goodsName);
            }
            if (!Objects.isNull(goodsCategory) && goodsCategory.length() > 0) {
                map.put("goodsCategory", goodsCategory);
            }
            if (!Objects.isNull(productionTime) && productionTime.length() > 0) {
                map.put("productionTime", new SimpleDateFormat("yyyy-MM-dd").parse(productionTime));
            }
            if (!Objects.isNull(status) && status.length() > 0) {
                map.put("status", status);
            }
//        Thread.sleep(1000);
            goodsService.selectByCondition(map, pageNum);
            PageInfo<GoodsDto> pageInfo = page.toPageInfo();
            mv.addObject("pageInfo", pageInfo);
            mv.addObject("userId", userId);
            mv.setViewName("goodsManage");
        } else {
            mv.setViewName("index");
        }
        return mv;
    }

    /**
     * 修改商品秒杀状态
     *
     * @param goodsId 商品ID
     */
    @RequestMapping(value = "seckillStatus", method = RequestMethod.POST)
    @ResponseBody
    public void selectGoods(
            @RequestParam(required = false, value = "goodsId") Long goodsId,
            @RequestParam(required = false, value = "status") String status
    ) throws Exception {
        GoodsDto goodsDto = goodsService.selectByGoodsId(goodsId);
        if (Objects.equals(status, "putaway")) {
            seckillService.addInventory(goodsDto);
            goodsDto.setStatus("P");
            goodsService.updateStatus(goodsDto);
        } else if (Objects.equals(status, "soldout")) {
            seckillService.deleteInventory(goodsDto);
            goodsDto.setStatus("S");
            goodsService.updateStatus(goodsDto);
        }
    }
}