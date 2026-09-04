package com.binhphuc.common_web_starter.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import com.binhphuc.common_core.context.UserContext;
import com.binhphuc.common_core.context.holder.UserContextHolder;
import com.binhphuc.common_core.enums.TrustedHeader;
import com.binhphuc.common_web_starter.config.UserContextProperties;
import com.binhphuc.common_web_starter.exception.handler.GlobalExceptionHandler;
import com.binhphuc.common_web_starter.filter.UserContextFilter;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(UserContextProperties.class)
public class AutoConfig {
    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public UserContextFilter userContextFilter(UserContextProperties userContextProperties) {
        return new UserContextFilter(userContextProperties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "userContextPropagationFilter")
    public ExchangeFilterFunction userContextPropagationFilter() {
        return (request, next) -> {
            UserContext userContext = UserContextHolder.getUserContext();
            if (userContext == null) {
                return next.exchange(request);
            }
            return next.exchange(ClientRequest.from(request)
                    .header(TrustedHeader.X_USER_ID.getHeaderName(), userContext.getUserId())
                    .header(TrustedHeader.X_USER_NAME.getHeaderName(), userContext.getUsername())
                    .build());
        };
    }
}
