package com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType;

/**
 * 令牌桶限流类型
 */
public enum LimitType {
	/**
	 * 令牌桶限流 默认
	 */
	TokenBucketLimiter,
	/**
	 * 根据ip限流
	 */
	IpLimiter,
	/**
	 * 根据IP和method限流
	 */
	IpAndMethodLimiter;

	LimitType() {
	}

	}
