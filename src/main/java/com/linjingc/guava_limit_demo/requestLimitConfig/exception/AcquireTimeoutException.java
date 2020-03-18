package com.linjingc.guava_limit_demo.requestLimitConfig.exception;


/**
 * 自定义获取令牌失败 错误
 *
 * @author cxc
 * @date 2019年8月8日18:16:08
 */
public class AcquireTimeoutException extends RuntimeException {

	public AcquireTimeoutException() {
	}

	public AcquireTimeoutException(String message) {
		super(message);
	}

	public AcquireTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}
}
