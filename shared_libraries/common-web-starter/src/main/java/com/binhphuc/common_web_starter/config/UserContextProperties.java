package com.binhphuc.common_web_starter.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "common.user-context")
public class UserContextProperties {
    private boolean required = false;

    private List<String> excludePatterns = new ArrayList<>(List.of("/actuator/**"));
}
