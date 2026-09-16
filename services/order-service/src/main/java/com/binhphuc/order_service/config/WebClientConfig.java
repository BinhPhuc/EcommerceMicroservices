package com.binhphuc.order_service.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.DeferringLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
    @Value("${services.product.base-url}")
    private String productBaseUrl;

    @Value("${services.product.timeout}")
    private Duration productTimeout;

    @Bean
    public WebClient productClient(DeferringLoadBalancerExchangeFilterFunction<?> loadBalancerFilter,
            @Qualifier("userContextPropagationFilter") ExchangeFilterFunction userContextPropagationFilter) {
        HttpClient httpClient = HttpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(productTimeout)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        return WebClient
                .builder()
                .baseUrl(productBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(loadBalancerFilter)
                .filter(userContextPropagationFilter)
                .build();
    }
}
