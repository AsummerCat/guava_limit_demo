package com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType;

/**
 * 限流类型
 */
public enum LimitType {
	/**
	 * 控制线程限流
	 */
	ThreadLimiter,
	/**
	 * 信号量限流
	 */
	semaphoreLimit,

	LimitType() {
	}

}
