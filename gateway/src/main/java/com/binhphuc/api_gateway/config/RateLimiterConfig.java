package com.binhphuc.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {
    @Bean
    public KeyResolver userIdKeyResolver() {
        return exchange -> {
            String userId = exchange.getPrincipal().cast(JwtAuthenticationToken.class).map(jwtAuth -> jwtAuth.getToken().getSubject()).toString();
            return Mono.just(userId);
        };
    }
}
