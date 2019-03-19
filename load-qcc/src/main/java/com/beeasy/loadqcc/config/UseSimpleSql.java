package com.beeasy.loadqcc.config;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface UseSimpleSql {
    String value() default "";
    boolean usePage() default true;
}