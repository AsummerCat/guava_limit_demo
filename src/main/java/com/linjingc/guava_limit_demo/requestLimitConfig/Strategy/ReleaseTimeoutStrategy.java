package com.linjingc.guava_limit_demo.requestLimitConfig.Strategy;

import com.linjingc.guava_limit_demo.requestLimitConfig.exception.AcquireTimeoutException;
import com.linjingc.guava_limit_demo.requestLimitConfig.handler.AcquireTokenFailureHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.time.LocalDateTime;


/**
 * 限流获取失败的 策略接口
 *
 * @author 一只写Bug的猫
 * @date 2019年8月8日18:21:28
 **/
public enum ReleaseTimeoutStrategy implements AcquireTokenFailureHandler {

	/**
	 * 继续执行业务逻辑，不做任何处理
	 */
	NO_OPERATION() {
		@Override
		public void handle(JoinPoint joinPoint) {
			// do nothing
		}
	},
	/**
	 * 快速失败
	 */
	FAIL_FAST() {
		@Override
		public void handle(JoinPoint joinPoint) {
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			String declaringTypeName = signature.getDeclaringTypeName();
			String controllerName;
			int lastIndex = declaringTypeName.lastIndexOf(".");
			if (lastIndex == -1) {
				controllerName = "";
			} else {
				controllerName = declaringTypeName.substring(lastIndex + 1);
			}
			String errorMsg = String.format("获取令牌失败 %s->%s方法,限流时间:%s,%s ", controllerName, signature.getMethod().getName(), LocalDateTime.now().toLocalDate(), "限流中.....");
			throw new AcquireTimeoutException(errorMsg);
		}
	}

}

