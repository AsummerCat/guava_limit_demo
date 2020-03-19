package com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.linjingc.guava_limit_demo.requestLimitConfig.model.LimitInfo;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 创建限流器的工厂
 *
 * @author 一只写Bug的猫
 * @date 2020年3月19日08:55:15
 */
@Component
public class CurrentLimitFactory {
	/**
	 * 缓存池
	 * 用来保存方法的限流策略
	 * 这边使用guava实现资源回收 避免过量增长
	 * 1天未读取就删除
	 */
	private static final Cache<Object, CurrentLimit> cache = CacheBuilder.newBuilder().maximumSize(100000).expireAfterAccess(1L, TimeUnit.DAYS).build();


	public CurrentLimit getLimit(LimitInfo limitInfo) {

		//判断缓存池是否有数据 没有数据就创建limit
		CurrentLimit limit = cache.getIfPresent(limitInfo.getName());
		if (Objects.isNull(limit)) {
			//创建limit
			return createLimit(limitInfo);
		}
		return limit;

	}

	/**
	 * 创建限流策略
	 *
	 * @param limitInfo
	 * @return
	 */
	private synchronized CurrentLimit createLimit(LimitInfo limitInfo) {
		CurrentLimit currentLimit = new TokenBucketLimiter(limitInfo);
		cache.put(limitInfo.getName(), currentLimit);
		return currentLimit;
	}

}