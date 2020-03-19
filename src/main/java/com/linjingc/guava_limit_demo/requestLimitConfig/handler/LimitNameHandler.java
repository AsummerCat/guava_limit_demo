package com.linjingc.guava_limit_demo.requestLimitConfig.handler;

import org.aspectj.lang.JoinPoint;

/**
 * 获取限流名称前缀的策略接口
 *
 * @author 一只写Bug的猫
 * @date 2020年3月19日08:47:57
 **/
public interface LimitNameHandler {

	/**
	 * 获取限流名称前缀
	 *
	 * @param joinPoint 切面内容
	 */
	String prefixName(JoinPoint joinPoint);
}
