package com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType;

/**
 * 限流实现接口
 * @author 一只写Bug的猫
 */
public interface CurrentLimit {

    /**
     * 限流
     *
     * @return
     */
    boolean acquire();

}