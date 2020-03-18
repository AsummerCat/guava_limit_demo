package com.linjingc.guava_limit_demo.requestLimitConfig.handler;

import org.aspectj.lang.JoinPoint;

/**
 * 获取限流的令牌失败处理策略接口
 *
 * @author cxc
 * @since 2019年8月8日18:19:18
 **/
public interface AcquireTokenFailureHandler {

	/**
	 * 处理
	 */
	void handle(JoinPoint joinPoint);
}
