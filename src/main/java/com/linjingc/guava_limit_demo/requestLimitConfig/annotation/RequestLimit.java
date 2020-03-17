package com.linjingc.guava_limit_demo.requestLimitConfig.annotation;


import com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType.LimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 *
 * @author 一只写Bug的猫
 * @date 2020年3月16日17:32:01
 */
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {

	/**
	 * 限流的速率
	 *
	 * @return
	 */
	double value() default 60d;

	/**
	 * 限流的名称 如果设置同样的name 表示多个方法共用一个限流池
	 */
	String name() default "";

	/**
	 * 限流类型
	 *
	 * @return
	 */
	LimitType limitType() default LimitType.ThreadLimiter;

	/**
	 * 自定义业务key
	 *
	 * @return keys
	 */
	String[] keys() default {};
}
