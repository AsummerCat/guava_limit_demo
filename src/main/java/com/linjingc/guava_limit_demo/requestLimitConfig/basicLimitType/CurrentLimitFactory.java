package com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType;

import com.linjingc.guava_limit_demo.requestLimitConfig.model.LimitInfo;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class CurrentLimitFactory {
	/**
	 * 缓存池
	 * 用来保存方法的限流策略
	 */
	private ConcurrentHashMap<String, CurrentLimit> limitMap = new ConcurrentHashMap<>();

	public CurrentLimit getLimit(LimitInfo limitInfo) {
		//判断缓存池是否有数据 没有数据就创建limit
		if (limitMap.containsKey(limitInfo.getName())) {
			return limitMap.get(limitInfo.getName());
		}
		//创建limit
		return createLimit(limitInfo, limitMap);
	}

	private synchronized CurrentLimit createLimit(LimitInfo limitInfo, ConcurrentHashMap<String, CurrentLimit> limitMap) {
		CurrentLimit currentLimit;
		switch (limitInfo.getType()) {
			case ThreadLimiter:
				currentLimit = new ThreadRateLimiter(limitInfo);
				break;
			case semaphoreLimit:
				currentLimit = new SemaphoreLimit(limitInfo);
				break;
			default:
				currentLimit = new ThreadRateLimiter(limitInfo);
				break;
		}
		limitMap.put(limitInfo.getName(), currentLimit);
		return currentLimit;
	}

}