package com.beeasy.hzback.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class UserIDAuditorBean implements AuditorAware<Long> {
    @Override
    public Long getCurrentAuditor() {
        return null;
    }
//    @Override
//    public Long getCurrentAuditor() {
//        SecurityContext ctx = SecurityContextHolder.getContext();
//        if (ctx == null) {
//            return null;
//        }
//        if (ctx.getAuthentication() == null) {
//            return null;
//        }
//        if (ctx.getAuthentication().getPrincipal() == null) {
//            return null;
//        }
//        Object principal = ctx.getAuthentication().getPrincipal();
//        if (principal.getClass().isAssignableFrom(Long.class)) {
//            return (Long) principal;
//        } else {
//            return null;
//        }
//    }
}

