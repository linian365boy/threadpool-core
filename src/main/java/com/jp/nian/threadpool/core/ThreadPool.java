package com.jp.nian.threadpool.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: ThreadPool  
 * @Description: 线程池 
 * @date: 2016年10月8日 下午2:08:22 
 * 
 * @author tanfan 
 * @version  
 * @since JDK 1.7
 */
public class ThreadPool implements Pool<PoolThread> {
	//默认线程池的线程数
	private static final int DEFAULT_SIZE = 6;
	//线程池中的空闲线程，用队列表示
	private BlockingQueue<PoolThread> idleThreads;
	//设置线程池是否关闭的标志
	private volatile boolean isShutdown = false;
	//日志组件
	private static final Logger logger = LoggerFactory.getLogger(ThreadPool.class);
	
	public ThreadPool(){
		this(DEFAULT_SIZE);
	}
	
	public ThreadPool(int threadSize){
		this.idleThreads = new LinkedBlockingQueue<>(threadSize);
		for(int i=0; i<threadSize; i++){
			PoolThread thread = new PoolThread(this);
			thread.setName("ThreadPool-"+i);
			thread.start();
			idleThreads.add(thread);
		}
	}
	
	@Override
	public void shutdown() {
		setShutdown(true);
		int threadSize = idleThreads.size();
		logger.debug("threadPool all has {} thread.", threadSize);
		threadSize = idleThreads.size();
		logger.debug("closing pool........");
		for(int i=0; i<threadSize; i++){
			PoolThread thread = borrowFromPool();
			thread.setTask(null);
			thread.setIdleLocal(null);
			thread.close();
		}
		idleThreads.clear();
	}
	
	@Override
	public void execute(Task task) {
		//线程空闲队列取出一个线程去执行任务
		PoolThread thread = borrowFromPool();
		logger.debug("I will set the task|{} soon...", task);
		thread.setTask(task);
	}
	
	@Override
	public PoolThread borrowFromPool() {
		PoolThread thread = null;
		try {
			logger.debug("borrow thread from pool, pool all has {} threads .", idleThreads.size());
			thread = idleThreads.take();
			logger.debug("thread {} borrow from pool, pool all has {} threads", thread.getName(), idleThreads.size());
		} catch (InterruptedException e) {
			logger.error("borrow from pool error",e);
		}
		return thread;
	}

	@Override
	public void returnToPool(PoolThread t) {
		try {
			logger.debug("thread {} return to pool", t.getName());
			idleThreads.offer(t, 1L, TimeUnit.SECONDS);
			logger.debug("thread {} return to pool, pool all has {} threads", t.getName(), idleThreads.size());
		} catch (InterruptedException e) {
			logger.error("return to pool error ",e);
		}
	}
	
	public boolean isShutdown() {
		return isShutdown;
	}

	public void setShutdown(boolean isShutdown) {
		this.isShutdown = isShutdown;
	}
}
