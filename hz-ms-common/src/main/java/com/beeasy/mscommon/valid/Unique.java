package com.beeasy.mscommon.valid;

import javax.validation.Constraint;
import javax.validation.constraints.AssertTrue;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Unique {
    String value() default "";
    String id() default "id";
}
