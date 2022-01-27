package com.info.redis;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.models.partitions.RedisClusterNode;
import io.lettuce.core.cluster.pubsub.RedisClusterPubSubAdapter;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.cluster.pubsub.api.async.NodeSelectionPubSubAsyncCommands;
import io.lettuce.core.cluster.pubsub.api.async.PubSubAsyncNodeSelection;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 绑定监听事件
 */
@Component
public class ClusterSubscriber extends RedisPubSubAdapter implements ApplicationRunner {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    private static Logger log = LoggerFactory.getLogger(ClusterSubscriber.class);

    //过期事件监听
    private static final String EXPIRED_CHANNEL = "__keyevent@0__:expired";

    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;

    @Value("${spring.redis.password}")
    private String password;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("过期事件，启动监听......");
        //项目启动后就运行该方法
        startListener();
    }

    /**
     * 启动监听
     */
    @SuppressWarnings("unchecked")
    public void startListener() {
        //redis集群监听
        String[] redisNodes = clusterNodes.split(",");
        //监听其中一个端口号即可
        RedisURI redisURI = RedisURI.create("redis://" + redisNodes[0]);
        redisURI.setPassword(password);
        RedisClusterClient clusterClient = RedisClusterClient.create(redisURI);

        StatefulRedisClusterPubSubConnection<String, String> pubSubConnection = clusterClient.connectPubSub();
        //redis节点间消息的传播为true
        pubSubConnection.setNodeMessagePropagation(true);
        //过期消息的接受和处理
        pubSubConnection.addListener(new RedisClusterPubSubAdapter() {
            @Override
            public void message(RedisClusterNode node, Object channel, Object message) {
                Integer userGoodsId = Integer.valueOf(String.valueOf(message).replace("\"", ""));
                Map<String, Object> userInfo = (Map<String, Object>) redisTemplate.boundHashOps("用户信息").get(userGoodsId);
                if (userInfo.get("status").equals("R") || userInfo.get("status").equals("W")) {
                    redisTemplate.boundHashOps("上架商品").put(userInfo.get("goodsId"), ((int) redisTemplate.boundHashOps("上架商品").get(userInfo.get("goodsId")) + (int) userInfo.get("orderNumber")));
                    redisTemplate.boundHashOps("用户信息").delete(userGoodsId);
                }
            }
        });

        //异步操作
        PubSubAsyncNodeSelection<String, String> masters = pubSubConnection.async().masters();
        NodeSelectionPubSubAsyncCommands<String, String> commands = masters.commands();
        //设置订阅消息类型，一个或多个
        commands.subscribe(EXPIRED_CHANNEL);
    }
}