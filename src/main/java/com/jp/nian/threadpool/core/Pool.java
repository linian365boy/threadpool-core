package com.jp.nian.threadpool.core;
/**
 * @ClassName: Pool  
 * @Description: 池接口 
 * @date: 2016年10月8日 下午2:08:02 
 * 
 * @author tanfan 
 * @version  
 * @param <T>
 * @since JDK 1.7
 */
public interface Pool<T> {
	//关闭线程池，会等待未完成任务的线程
	void shutdown();
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
