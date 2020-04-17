package com.linjingc.guava_limit_demo;

import java.math.BigDecimal;

public class test {
	public static void main(String[] args) {

		BigDecimal a = new BigDecimal(4.92);
		BigDecimal b = new BigDecimal(3);

		BigDecimal divide = a.setScale( 1, BigDecimal.ROUND_UP);
		System.out.println(divide);

	}
	/**
	 * select trunc(4.136,2) as 直接丢弃尾巴,
	 * round(4.136,2) as 四舍五入
	 *
	 *  from dual
	 */
}
