package com.binhphuc.api_gateway.filter;

import com.binhphuc.common_core.enums.TrustedHeader;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class UserContextFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<String> trustedHeaders = Arrays.stream(TrustedHeader.values())
                .map(TrustedHeader::getHeaderName)
                .toList();
        return exchange.getPrincipal().cast(JwtAuthenticationToken.class).map(jwtAuth -> {
            String userId = jwtAuth.getToken().getSubject();
            String username = jwtAuth.getToken().getClaimAsString("preferred_username");
            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .headers(httpHeaders -> trustedHeaders.forEach(httpHeaders::remove))
                    .header(TrustedHeader.X_USER_ID.getHeaderName(), userId)
                    .header(TrustedHeader.X_USER_NAME.getHeaderName(), username != null ? username : "")
                    .header(TrustedHeader.X_REQUEST_ID.getHeaderName(), UUID.randomUUID().toString())
                    .build();
            return exchange.mutate().request(mutatedRequest).build();
        }).defaultIfEmpty(exchange).flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
