package com.info.controller;

import com.info.impl.GoodsServiceImpl;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
public class SeckillController {
    @Resource
    private GoodsServiceImpl goodsService;
    @Resource
    private AmqpTemplate amqpTemplate;
    @Resource
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 测试页面
     */
    @RequestMapping("seckill")
    @ResponseBody
    public ModelAndView test(ModelAndView mv) {
        mv.setViewName("seckill");
        return mv;
    }


    @RequestMapping("send")
    public ModelAndView send(ModelAndView mv) throws InterruptedException {
        String msg = "hello, Spring boot amqp";
        this.amqpTemplate.convertAndSend("spring.test.exchange", "a.b", msg);
        // 等待10秒后再结束
        Thread.sleep(10000);
        mv.setViewName("seckill");
        return mv;
    }

    @RequestMapping("redis")
    public ModelAndView redis(ModelAndView mv) {
        redisTemplate.opsForValue().set("name", "admin");
        Object name = redisTemplate.opsForValue().get("ame");
        System.out.println("name" + name);
        redisTemplate.delete("name");
        mv.setViewName("seckill");
        return mv;
    }
//
//    /**
//     * 保存/修改商品信息
//     *
//     * @param goodsDto 需要保存的商品对象
//     */
//    @RequestMapping(value = "save", method = RequestMethod.POST)
//    @ResponseBody
//    public int save(@ModelAttribute(value = "save") GoodsDto goodsDto) throws Exception {
//        return goodsService.save(goodsDto);
//    }
//
//    /**
//     * 删除商品
//     *
//     * @param goodsId 商品ID
//     */
//    @GetMapping("delete")
//    @ResponseBody
//    public int delete(@RequestParam(required = false, value = "goodsId") Long goodsId) {
//        return goodsService.deleteByGoodsId(goodsId);
//    }
//
//    /**
//     * 根据ID查询商品
//     * @param goodsId 商品ID
//     */
//    @RequestMapping(value = "selectGoods", method = RequestMethod.POST)
//    @ResponseBody
//    public GoodsDto selectGoods(
//            @RequestParam(required = false, value = "goodsId") Long goodsId) throws Exception {
//        return goodsService.selectByGoodsId(goodsId);
//    }
//
//    /**
//     * 根据条件查询商品
//     *
//     * @param goodsName      商品名称
//     * @param goodsCategory  商品类型
//     * @param productionTime 生产日期
//     * @param pageNum        当前页
//     * @param pageSize       每页显示的数据条数
//     */
//    @RequestMapping(value = "seckill", method = RequestMethod.GET)
//    public ModelAndView selectGoodsList(
//            @RequestParam(required = false, value = "goodsName") String goodsName,
//            @RequestParam(required = false, value = "goodsCategory") String goodsCategory,
//            @RequestParam(required = false, value = "productionTime") String productionTime,
//            @RequestParam(required = false, defaultValue = "1", value = "pageNum") int pageNum,
//            @RequestParam(required = false, defaultValue = "10", value = "pageSize") int pageSize, ModelAndView mv) throws Exception {
//        Page<GoodsDto> page = PageHelper.startPage(pageNum, pageSize);
//        Map<String, Object> map = new HashMap<>();
//        if (!Objects.isNull(goodsName) && goodsName.length() > 0) {
//            map.put("goodsName", goodsName);
//        }
//        if (!Objects.isNull(goodsCategory) && goodsCategory.length() > 0) {
//            map.put("goodsCategory", goodsCategory);
//        }
//        if (!Objects.isNull(productionTime) && productionTime.length() > 0) {
//            map.put("productionTime", new SimpleDateFormat("yyyy-MM-dd").parse(productionTime));
//        }
////        Thread.sleep(1000);
//        goodsService.selectByCondition(map, pageNum);
//        PageInfo<GoodsDto> pageInfo = page.toPageInfo();
//        mv.addObject("pageInfo", pageInfo);
//        mv.setViewName("goodsManage");
//        return mv;
//    }


}