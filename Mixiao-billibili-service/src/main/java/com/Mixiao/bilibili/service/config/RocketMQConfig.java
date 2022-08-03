package com.Mixiao.bilibili.service.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.Mixiao.bilibili.domain.UserFollowing;
import com.Mixiao.bilibili.domain.UserMoment;
import com.Mixiao.bilibili.domain.constant.UserConstant;
import com.Mixiao.bilibili.domain.constant.UserMomentsConstant;
import com.Mixiao.bilibili.service.UserFollowingService;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserFollowingService userFollowingService;
    //生产者
    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_MOMENTS);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

    //消费者
    @Bean("momentsConsumer")
    public DefaultMQPushConsumer momentsConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_MOMENTS);
        consumer.setNamesrvAddr(nameServerAddr);
        consumer.subscribe(UserMomentsConstant.TOPIC_MOMENTS, "*");//订阅 ∴
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context){
                MessageExt msg = msgs.get(0);
                if(msg == null){
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                String bodyStr = new String(msg.getBody());
                UserMoment userMoment = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), UserMoment.class);
                Long userId = userMoment.getUserId();
                List<UserFollowing>fanList = userFollowingService.getUserFans(userId);
                for(UserFollowing fan : fanList){
                    String key = "subscribed-" + fan.getUserId();
                    String subscribedListStr = redisTemplate.opsForValue().get(key);
                    List<UserMoment> subscribedList;
                    if(StringUtil.isNullOrEmpty(subscribedListStr)){
                        subscribedList = new ArrayList<>();
                    }else{
                        subscribedList = JSONArray.parseArray(subscribedListStr, UserMoment.class);
                    }
                    subscribedList.add(userMoment);
                    redisTemplate.opsForValue().set(key, JSONObject.toJSONString(subscribedList));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }
}
