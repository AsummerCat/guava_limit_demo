package com.linjingc.guava_limit_demo.requestLimitConfig;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 限流注解
 *
 * @author 一只写Bug的猫
 * @date 2020年3月16日17:32:01
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {
	int value() default 10;
}
