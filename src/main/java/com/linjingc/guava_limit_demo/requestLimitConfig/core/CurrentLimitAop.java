package com.linjingc.guava_limit_demo.requestLimitConfig.core;

import com.linjingc.guava_limit_demo.requestLimitConfig.annotation.RequestLimit;
import com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType.CurrentLimit;
import com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType.CurrentLimitFactory;
import com.linjingc.guava_limit_demo.requestLimitConfig.exception.AcquireTimeoutException;
import com.linjingc.guava_limit_demo.requestLimitConfig.model.LimitInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 限流切面类
 *
 * @author 一只写Bug的猫
 * @date 2020年3月19日08:46:24
 */
@Aspect
@Component  //声明首先加载入spring
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

		if (!currentLimit.acquire()) {
			//获取令牌失败的处理策略
			requestLimit.ReleaseTimeoutStrategy().handle(joinPoint);
		}
		return joinPoint.proceed();
	}


	/**
	 * 异常处理
	 *
	 * @param point
	 * @param requestLimit
	 * @param ex
	 */
	@AfterThrowing(value = "@annotation(requestLimit)", throwing = "ex")
	public void afterReturning(JoinPoint point, RequestLimit requestLimit, Exception ex) {
//		String methodName = point.getSignature().getName();
//		List<Object> args = Arrays.asList(point.getArgs());
//		System.out.println("连接点方法为：" + methodName + ",参数为：" + args + ",异常为：" + ex);

		//判断异常是否为限流导致的
		if (ex instanceof AcquireTimeoutException) {
			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
			HttpServletResponse response = sra.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=utf-8");
			try (ServletOutputStream out = response.getOutputStream()) {
				out.write(ex.getMessage().getBytes(StandardCharsets.UTF_8));
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// do nothing
		}

	}
}
