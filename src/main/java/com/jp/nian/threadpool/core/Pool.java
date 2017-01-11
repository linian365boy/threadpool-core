package com.jp.nian.threadpool.core;
/**
 * @ClassName: Pool  
 * @Description: 线程池接口 
 * @date: 2017年1月11日 上午11:43:06 
 * 
 * @author tanfan 
 * @version @param <T> 
 * @since JDK 1.7
 */
public interface Pool<T> {
	//关闭线程池，会等待未完成任务的线程
	void shutdown();
	//立即关闭线程池，不会等待线程完成
	void shutdownnow();
	//执行任务
	void execute(Task task);
	//借
	T borrowFromPool();
	//还
	void returnToPool(T t);
	//得到线程池关闭标志
	public boolean isShutdown();
	//设置线程池关闭标志
	public void setShutdown(boolean isShutdown) ;
	
}
