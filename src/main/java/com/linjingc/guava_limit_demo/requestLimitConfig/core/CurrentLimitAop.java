package com.linjingc.guava_limit_demo.requestLimitConfig.core;

import com.linjingc.guava_limit_demo.requestLimitConfig.annotation.RequestLimit;
import com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType.CurrentLimit;
import com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType.CurrentLimitFactory;
import com.linjingc.guava_limit_demo.requestLimitConfig.model.LimitInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * 限流切面类
 *
 * @author 一只写Bug的猫
 */
@Aspect
@Component
//声明首先加载入spring
@Order(0)
public class CurrentLimitAop {
	@Autowired
	BusinessKeyProvider businessKeyProvider;
	@Autowired
	CurrentLimitFactory currentLimitFactory;

	@Around(value = "@annotation(requestLimit)")
	public Object around(ProceedingJoinPoint joinPoint, RequestLimit requestLimit) throws Throwable {

		//获取出限流的基础信息
		LimitInfo limitInfo = businessKeyProvider.get(joinPoint, requestLimit);
		//根据工厂模式 获取到CurrentLimit
		CurrentLimit currentLimit = currentLimitFactory.getLimit(limitInfo);
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
		HttpServletRequest request = sra.getRequest();
		if (!currentLimit.acquire()) {
			String data = "限流中";
			return data;
//			throw new Exception("暂未获取到数据");
		}

		System.out.println("validateArgs" + Arrays.asList(joinPoint.getArgs()));
		return joinPoint.proceed();

	}

}