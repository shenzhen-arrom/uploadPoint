package com.arrom.uploadpoint.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 专门用来标记我们上传的用户行为的注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@CommonAnnotationBase(type = "event",actionId = "10001")
public @interface MaiDianData {
    String value() default "";

}
