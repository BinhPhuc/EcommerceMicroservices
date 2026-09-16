package com.binhphuc.common_jpa_starter.autoconfig;

import java.util.Optional;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

import com.binhphuc.common_core.context.UserContext;
import com.binhphuc.common_core.context.holder.UserContextHolder;

@AutoConfiguration
@ConditionalOnClass(AuditorAware.class)
public class AutoConfig {
    private static final String SYSTEM_AUDITOR = "system";

    @Bean
    @ConditionalOnMissingBean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(UserContextHolder.getUserContext())
                .map(UserContext::getUsername)
                .or(() -> Optional.of(SYSTEM_AUDITOR));
    }
}
