package com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType;

import com.google.common.util.concurrent.RateLimiter;
import com.linjingc.guava_limit_demo.requestLimitConfig.model.LimitInfo;

/**
 * 使用限流
 * 基于线程实现
 */
public class ThreadRateLimiter implements CurrentLimit {
	private RateLimiter limiter;
	private LimitInfo limitInfo;

	public ThreadRateLimiter(LimitInfo limitInfo) {
		this.limitInfo = limitInfo;

		this.limiter = RateLimiter.create(0.5d);
	}

	@Override
	public boolean acquire() {
		return limiter.tryAcquire();
	}
}
