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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取用户定义业务key
 * 生成限流info的内容
 *
 * @author 一只写Bug的猫
 * @date 2020年3月19日08:45:32
 */
@Component
public class BusinessKeyProvider {

	public LimitInfo get(JoinPoint joinPoint, RequestLimit requestLimit) {
		//获取到切面的信息
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		//获取到限流类型 ,并且获取到前缀名称
		LimitType type = requestLimit.limitType();
		//根据自定义业务key 获取keyName
		String businessKeyName = getKeyName(joinPoint, requestLimit);
		//根据自定义name配置 如果存在name 则使用name,否则使用方法名当做name
		String limitName = type.prefixName(joinPoint) + ":" + getName(requestLimit.name(), signature) + businessKeyName;
		//根据过期时间添加 时间戳
		if(requestLimit.delayTime()>0){
			//计算毫秒
			long delayMillis = requestLimit.unit().toMillis(requestLimit.delayTime());
			return new LimitInfo(limitName, type, requestLimit.value(),delayMillis);
		}else{
			return new LimitInfo(limitName, type, requestLimit.value());
		}
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

}
