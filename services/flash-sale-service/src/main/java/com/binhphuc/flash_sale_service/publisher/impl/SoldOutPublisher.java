package com.binhphuc.flash_sale_service.publisher.impl;

import com.binhphuc.flash_sale_service.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoldOutPublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    @Qualifier("soldOutTopic")
    private final ChannelTopic topic;

    @Override
    public void publish(Object message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}