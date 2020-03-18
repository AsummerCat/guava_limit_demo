package com.linjingc.guava_limit_demo.requestLimitConfig.handler;

import org.aspectj.lang.JoinPoint;

/**
 * 获取限流名称前缀的策略接口
 **/
public interface LimitNameHandler {

	/**
	 * 获取限流名称前缀
	 *
	 * @param joinPoint 切面内容
	 */
	String prefixName(JoinPoint joinPoint);
}
