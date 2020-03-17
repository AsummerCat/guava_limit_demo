package com.linjingc.guava_limit_demo.controller;

import com.linjingc.guava_limit_demo.requestLimitConfig.annotation.RequestLimit;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("hello")
public class HelloController {

	@RequestMapping("index")
	@RequestLimit
	public String index(HttpServletRequest httpServletRequest) {
		return "这是首页";
	}
}
