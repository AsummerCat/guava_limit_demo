package com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType;

import com.google.common.util.concurrent.RateLimiter;
import com.linjingc.guava_limit_demo.requestLimitConfig.model.LimitInfo;
import lombok.Data;

/**
 * 使用限流
 * 基于信号量实现
 */
@Data
public class SemaphoreLimit implements CurrentLimit {
	private RateLimiter limiter;
	private LimitInfo limitInfo;

	public SemaphoreLimit(LimitInfo limitInfo) {
		this.limitInfo = limitInfo;

		this.limiter = RateLimiter.create(0.5d);
	}

	@Override
	public boolean acquire() {
		return limiter.tryAcquire();
	}
}
