package com.info.rabbitmq;

import com.info.service.ShoppingService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Listener {
    @Resource
    private ShoppingService shoppingService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "seckill.queue", durable = "true"),
            exchange = @Exchange(
                    value = "seckill.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"seckillGoods"}))
    public void listen(String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Exception {
        if (msg.contains("success")) {
            // 正确消费时，通过调用 basicAck 方法即可,RabbitMQ的ack机制中，第二个参数返回true，表示需要将这条消息投递给其他的消费者重新消费
            channel.basicAck(deliveryTag, false);
            Map<Long, Long> map = new HashMap<>();
//            for (Object o : redisTemplate.boundHashOps("用户信息").entries().keySet()) {
//                shoppingService.saveSeckillRecord(Long.valueOf(String.valueOf(redisTemplate.boundHashOps("用户信息").entries().get(o))).longValue(), Long.valueOf(String.valueOf(o)).longValue() - Long.valueOf(String.valueOf(redisTemplate.boundHashOps("用户信息").entries().get(o))).longValue());
//            }
            //压测代码
            shoppingService.saveSeckillRecord(redisTemplate.boundHashOps("用户信息").entries());
            redisTemplate.delete("用户信息");
        } else {
            //消费失败时，需要将消息重新塞入队列，等待重新消费时，可以使用 basicNack,第三个参数true，表示这个消息会重新进入队列
            channel.basicNack(deliveryTag, false, true);
        }
    }
}