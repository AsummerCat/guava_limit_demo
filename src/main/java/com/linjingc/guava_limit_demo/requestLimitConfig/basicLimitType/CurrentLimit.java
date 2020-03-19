package com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType;

import org.aspectj.lang.JoinPoint;

/**
 * 限流实现接口
 *
 * @author 一只写Bug的猫
 * @date 2020年3月19日08:55:28
 */
public interface CurrentLimit {

	/**
	 * 限流
	 *
	 * @return
	 */
	boolean acquire();
}