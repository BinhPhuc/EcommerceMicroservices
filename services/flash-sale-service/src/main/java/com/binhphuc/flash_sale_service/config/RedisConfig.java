package com.binhphuc.flash_sale_service.config;

import com.binhphuc.flash_sale_service.subscriber.SoldOutMessageSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
public class RedisConfig {
    @Bean
    MessageListenerAdapter soldOutMessageListener(SoldOutMessageSubscriber soldOutMessageSubscriber) {
        return new MessageListenerAdapter(soldOutMessageSubscriber);
    }

    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory,
                                                 MessageListenerAdapter soldOutMessageListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(soldOutMessageListener, soldOutTopic());
        return container;
    }

    @Bean(name = "soldOutTopic")
    ChannelTopic soldOutTopic() {
        return new ChannelTopic("soldOut");
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setValueSerializer(GenericJacksonJsonRedisSerializer.builder().build());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisScript<List> reserveStockScript() {
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("scripts/reserve_stock.lua"));
        redisScript.setResultType(List.class);
        return redisScript;
    }

    @Bean
    public RedisScript<List> releaseStockScript() {
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("scripts/release_stock.lua"));
        redisScript.setResultType(List.class);
        return redisScript;
    }
}
