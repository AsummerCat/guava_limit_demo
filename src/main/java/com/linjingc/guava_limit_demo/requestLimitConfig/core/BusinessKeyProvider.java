package com.linjingc.guava_limit_demo.requestLimitConfig.core;


import com.linjingc.guava_limit_demo.requestLimitConfig.annotation.RequestLimit;
import com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType.LimitType;
import com.linjingc.guava_limit_demo.requestLimitConfig.model.LimitInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取用户定义业务key
 *
 * @author cxc
 * @date 2019年08月08日20:52:40
 */
@Component
public class BusinessKeyProvider {

	public  LimitInfo get(JoinPoint joinPoint, RequestLimit requestLimit) {
		//获取到切面的信息
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		//获取到限流类型
		LimitType type = requestLimit.limitType();
		//根据自定义业务key 获取keyName
		String businessKeyName = getKeyName(joinPoint, requestLimit);
		//拼接limitName
		String limitName = getName(requestLimit.name(), signature) + businessKeyName;

		//获取访问的ip
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
		HttpServletRequest request = sra.getRequest();
		String ipAddress = getIpAddress(request);


		//实例化限流
		return new LimitInfo(limitName, type,requestLimit.value(),ipAddress);
	}


	private ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

	private ExpressionParser parser = new SpelExpressionParser();


	/**
	 * 获取限流的名称
	 *
	 * @param annotationName
	 * @param signature
	 * @return
	 */
	private String getName(String annotationName, MethodSignature signature) {
		//如果keyname没有设置 则返回方法名称
		if (annotationName.isEmpty()) {
			return String.format("%s.%s", signature.getDeclaringTypeName(), signature.getMethod().getName());
		} else {
			return annotationName;
		}
	}


	public String getKeyName(JoinPoint joinPoint, RequestLimit requestLimit) {
		List<String> keyList = new ArrayList<>();
		Method method = getMethod(joinPoint);
		//获取方法RequestLimit注解上的自定义keys
		List<String> definitionKeys = getSpelDefinitionKey(requestLimit.keys(), method, joinPoint.getArgs());
		keyList.addAll(definitionKeys);
		//进行拼接
		return StringUtils.collectionToDelimitedString(keyList, "", "-", "");
	}

	/**
	 * 获取到切到的当前方法
	 */
	private Method getMethod(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		if (method.getDeclaringClass().isInterface()) {
			try {
				method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), method.getParameterTypes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return method;
	}

	/**
	 * 获取方法RequestLimit注解上的自定义keys
	 */
	private List<String> getSpelDefinitionKey(String[] definitionKeys, Method method, Object[] parameterValues) {
		List<String> definitionKeyList = new ArrayList<>();
		for (String definitionKey : definitionKeys) {
			if (definitionKey != null && !definitionKey.isEmpty()) {
				EvaluationContext context = new MethodBasedEvaluationContext(null, method, parameterValues, nameDiscoverer);
				String key = parser.parseExpression(definitionKey).getValue(context).toString();
				definitionKeyList.add(key);
			}
		}
		return definitionKeyList;
	}

	/**
	 * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
	 *
	 * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
	 * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
	 *
	 * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
	 * 192.168.1.100
	 *
	 * 用户真实IP为： 192.168.1.110
	 */
	public  String getIpAddress(HttpServletRequest request) {
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
