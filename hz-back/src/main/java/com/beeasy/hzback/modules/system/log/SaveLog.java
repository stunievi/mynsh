package com.beeasy.hzback.modules.system.log;

import com.beeasy.common.entity.SystemTextLog;
import io.swagger.annotations.ApiOperation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SaveLog {
//    public SystemTextLog.Type type();

    public String value() default "";
}
