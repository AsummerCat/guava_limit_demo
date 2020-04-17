package com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType;

import com.linjingc.guava_limit_demo.requestLimitConfig.handler.LimitNameHandler;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 枚举
 * 令牌桶限流类型
 * 并且获取限流前缀名称
 *
 * @author 一只写Bug的猫
 * @date 2020年3月19日08:55:15
 */
public enum LimitType implements LimitNameHandler {
	/**
	 * 令牌桶限流 默认
	 */
	TokenBucketLimiter() {
		@Override
		public String prefixName(JoinPoint joinPoint) {
			String name = this.getClass().getName();
			return name;
		}
	},


	/**
	 * 根据IP和method限流
	 */
	IpAndMethodLimiter() {
		@Override
		public String prefixName(JoinPoint joinPoint) {
			//获取访问的ip
			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
			HttpServletRequest request = sra.getRequest();
			String ipAddress = getIpAddress(request);
			String name = this.getClass().getName() + ":" + ipAddress;
			return name;

		}
	},


	/**
	 * 根据ip限流
	 */
	IpLimiter() {
		@Override
		public String prefixName(JoinPoint joinPoint) {
			//获取访问的ip
			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
			HttpServletRequest request = sra.getRequest();
			String ipAddress = getIpAddress(request);
			return ipAddress;
		}
	};

	/**
	 * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
	 * <p>
	 * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
	 * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
	 * <p>
	 * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
	 * 192.168.1.100
	 * <p>
	 * 用户真实IP为： 192.168.1.110
	 */
	public String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

}
