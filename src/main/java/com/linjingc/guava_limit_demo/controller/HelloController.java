package com.linjingc.guava_limit_demo.controller;

import com.linjingc.guava_limit_demo.requestLimitConfig.Strategy.ReleaseTimeoutStrategy;
import com.linjingc.guava_limit_demo.requestLimitConfig.annotation.RequestLimit;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 测试接口
 *
 * @author 一只写Bug的猫
 * @date 2020年3月19日08:55:15
 */
@RestController
@RequestMapping("hello")
public class HelloController {

	@RequestMapping("index")
	@RequestLimit(ReleaseTimeoutStrategy = ReleaseTimeoutStrategy.FAIL_FAST, value = 0.5d)
	public String index(HttpServletRequest httpServletRequest) {
		return "这是首页";
	}
}
