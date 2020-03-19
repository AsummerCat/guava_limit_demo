package com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType;

import com.google.common.util.concurrent.RateLimiter;
import com.linjingc.guava_limit_demo.requestLimitConfig.model.LimitInfo;

/**
 * 使用限流
 * 基于令牌桶实现
 *
 * @author 一只写Bug的猫
 * @date 2020年3月19日08:55:15
 */
public class TokenBucketLimiter implements CurrentLimit {
	private RateLimiter limiter;
	private LimitInfo limitInfo;

	public TokenBucketLimiter(LimitInfo limitInfo) {
		this.limitInfo = limitInfo;
		this.limiter = RateLimiter.create(limitInfo.getValue());
	}

	@Override
	public boolean acquire() {
		return limiter.tryAcquire();
	}
}
