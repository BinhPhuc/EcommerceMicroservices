package com.binhphuc.order_service.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ConfigurationProperties(prefix = "services.product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductClientProperties {
    private String baseUrl;
    private Duration timeout;
}
