package com.linjingc.guava_limit_demo.requestLimitConfig.model;

import com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType.LimitType;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * 当前限流属性
 *
 * @author 一只写Bug的猫
 * @since 2019年8月8日18:19:18
 */
@Data
public class LimitInfo {

	public LimitInfo(String name, LimitType type, Double value, Long delayTime) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.delayTime = delayTime;
	}

	public LimitInfo(String name, LimitType type, Double value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	/**
	 * 限流的名称
	 */
	private String name;
	/**
	 * 限流的类型
	 */
	private LimitType type;

	/**
	 * 限流的速率
	 *
	 * @return
	 */
	private Double value;
	/**
	 * 延迟时间 -> 多久过期
	 * 毫秒
     */
	private Long delayTime =0L;





}
