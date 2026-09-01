package com.binhphuc.flash_sale_service.config;

import com.binhphuc.flash_sale_service.context.holder.UserContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class AuditConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAware<String>() {
            @Override
            public Optional<String> getCurrentAuditor() {
                String username = UserContextHolder.getUserContext().getUsername();
                return Optional.of(username);
            }
        };
    }

}
