package com.binhphuc.flash_sale_service.config;

import com.binhphuc.flash_sale_service.constant.OrderOutboxConstant;
import com.binhphuc.flash_sale_service.service.OrderOutboxService;
import com.binhphuc.flash_sale_service.subscriber.OrderOutboxSubscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.util.UUID;

@Configuration
public class OrderOutboxConfig {
    @Bean(name = "outboxConsumerName")
    public String outboxConsumerName(@Value("${spring.application.name}") String applicationName) {
        return applicationName + "-" + UUID.randomUUID();
    }

    @Bean(destroyMethod = "stop")
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> orderOutboxListenerContainer(
            RedisConnectionFactory redisConnectionFactory) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String,
                MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(OrderOutboxConstant.OUTBOX_POLL_TIMEOUT)
                        .serializer(new StringRedisSerializer())
                        .build();
        return StreamMessageListenerContainer.create(redisConnectionFactory, options);
    }

    @Bean
    public Subscription orderOutboxSubscription(
            StreamMessageListenerContainer<String, MapRecord<String, String, String>> orderOutboxListenerContainer,
            OrderOutboxSubscriber orderOutboxSubscriber,
            OrderOutboxService orderOutboxService,
            String outboxConsumerName) {
        orderOutboxService.createConsumerGroup();
        Subscription subscription = orderOutboxListenerContainer.receive(
                Consumer.from(OrderOutboxConstant.OUTBOX_CONSUMER_GROUP, outboxConsumerName),
                StreamOffset.create(OrderOutboxConstant.OUTBOX_STREAM_KEY, ReadOffset.lastConsumed()),
                orderOutboxSubscriber);
        orderOutboxListenerContainer.start();
        return subscription;
    }
}
