package com.beeasy.hzback.modules.system.log;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SaveLog {
//    public SystemTextLog.Type type();

    public String value() default "";
}
