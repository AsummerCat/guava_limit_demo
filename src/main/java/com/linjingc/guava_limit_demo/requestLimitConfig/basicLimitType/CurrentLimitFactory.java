package com.linjingc.guava_limit_demo.requestLimitConfig.basicLimitType;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.linjingc.guava_limit_demo.requestLimitConfig.model.LimitInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * 创建限流器的工厂
 *
 * @author 一只写Bug的猫
 * @date 2020年3月19日08:55:15
 */
@Component
public class CurrentLimitFactory implements InitializingBean {

	/**
	 * 缓存池
	 * 用来保存方法的限流策略
	 * 这边使用guava实现资源回收 避免过量增长
	 * 15天未读取就删除
	 */
	private static volatile Cache<Object, CurrentLimit> cache = CacheBuilder.newBuilder().maximumSize(100000).expireAfterAccess(15L, TimeUnit.DAYS).build();
	/**
	 * 手动过期删除策略
	 */
	private static ExecutorService executorService = Executors.newSingleThreadExecutor();
	private static volatile DelayQueue<DelayedTask> delayQueue = new DelayQueue<>();


	public CurrentLimit getLimit(LimitInfo limitInfo) {
		//判断缓存池是否有数据 没有数据就创建limit
		CurrentLimit limit = cache.getIfPresent(limitInfo.getName());
		if (Objects.isNull(limit)) {
			//创建limit
			return createLimit(limitInfo);
		}
		return limit;

	}

	/**
	 * 创建限流策略
	 *
	 * @param limitInfo
	 * @return
	 */
	private synchronized CurrentLimit createLimit(LimitInfo limitInfo) {
		CurrentLimit limit = cache.getIfPresent(limitInfo.getName());
		CurrentLimit currentLimit;
		//双重检测
		if (Objects.isNull(limit)) {
			currentLimit = new TokenBucketLimiter(limitInfo);
			cache.put(limitInfo.getName(), currentLimit);
				//添加超时任务
				addTimeoutTask(limitInfo);
		} else {
			currentLimit = limit;
		}
		return currentLimit;
	}


	/**
	 * 过期手动删除 这里先用类似redis的定时删除 和惰性删除模式
	 */
	private void addTimeoutTask(LimitInfo limitInfo){
		if(limitInfo.getDelayTime()>0){
		DelayedTask element = new DelayedTask(limitInfo.getDelayTime(),limitInfo);
		delayQueue.offer(element);
			System.out.println("添加超时任务到队列:->"+element.msg.getName());
		}
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		/**
		 * 开启监控线程 监控超时移除
		 */
		executorService.execute(() -> {
			System.out.println("启动监控限流超时的队列线程启动中。。。。。。。。。。");
					while (true) {
						DelayedTask element;
						try {
							element = delayQueue.take();
							//移除过期key
							System.out.println("开始移除key:->"+element.msg.getName());
							cache.invalidate(element.msg.getName());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
		);
	}


	/**
	 * 延时队列的内容对象 过期删除
	 */
	static class DelayedTask implements Delayed {
		private final long delay; //延迟时间
		private final long expire;  //到期时间
		private final LimitInfo msg;   //数据
		private final long now; //创建时间

		public DelayedTask(long delay, LimitInfo msg) {
			this.delay = delay;
			this.msg = msg;
			expire = System.currentTimeMillis() + delay;    //到期时间 = 当前时间+延迟时间
			now = System.currentTimeMillis();
		}

		/**
		 * 需要实现的接口，获得延迟时间   用过期时间-当前时间
		 *
		 * @param unit
		 * @return
		 */
		@Override
		public long getDelay(TimeUnit unit) {
			//根据过期时间-当前时间
			return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		}

		/**
		 * 用于延迟队列内部比较排序   当前时间的延迟时间 - 比较对象的延迟时间
		 *
		 * @param o
		 * @return
		 */
		@Override
		public int compareTo(Delayed o) {
			return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("DelayedTask{");
			sb.append("delay=").append(delay);
			sb.append(", expire=").append(expire);
			sb.append(", msg='").append(msg.getName()).append('\'');
			sb.append(", now=").append(now);
			sb.append('}');
			return sb.toString();
		}
	}

}