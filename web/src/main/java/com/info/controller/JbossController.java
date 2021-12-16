package com.info.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.info.dto.GoodsDto;
import com.info.impl.GoodsServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller("/")
public class JbossController {
    @Resource
    private GoodsServiceImpl goodsService;

    /**
     * 测试页面
     * @param goodsId 商品ID
     */
    @GetMapping("test")
    @ResponseBody
    public String test(@RequestParam(required = false, value = "goodsId") Long goodsId) {
        return "test";
    }

    /**
     * 保存/修改商品信息
     *
     * @param goodsDto 需要保存的商品对象
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public int save(@ModelAttribute(value = "save") GoodsDto goodsDto) throws Exception {
        return goodsService.save(goodsDto);
    }

    /**
     * 删除商品
     *
     * @param goodsId 商品ID
     */
    @GetMapping("delete")
    @ResponseBody
    public int delete(@RequestParam(required = false, value = "goodsId") Long goodsId) {
        return goodsService.deleteByGoodsId(goodsId);
    }

    /**
     * 根据ID查询商品
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
    @RequestMapping(value = "selectGoodsList", method = RequestMethod.GET)
    public ModelAndView selectGoodsList(
            @RequestParam(required = false, value = "goodsName") String goodsName,
            @RequestParam(required = false, value = "goodsCategory") String goodsCategory,
            @RequestParam(required = false, value = "productionTime") String productionTime,
            @RequestParam(required = false, defaultValue = "1", value = "pageNum") int pageNum,
            @RequestParam(required = false, defaultValue = "10", value = "pageSize") int pageSize, ModelAndView mv) throws Exception {
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
//        Thread.sleep(1000);
        goodsService.selectByCondition(map, pageNum);
        PageInfo<GoodsDto> pageInfo = page.toPageInfo();
        mv.addObject("pageInfo", pageInfo);
        mv.setViewName("query");
        return mv;
    }


}