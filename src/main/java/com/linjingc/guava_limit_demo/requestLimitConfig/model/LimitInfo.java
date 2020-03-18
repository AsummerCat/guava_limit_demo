package com.linjingc.guava_limit_demo.requestLimitConfig.model;

import com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType.LimitType;
import lombok.Data;

/**
 * 当前限流属性
 */
@Data
public class LimitInfo {

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



}
