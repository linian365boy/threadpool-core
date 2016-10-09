package com.jp.nian.threadpool.core;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: PoolThread  
 * @Description: 线程池中的线程 
 * @date: 2016年10月8日 下午2:23:07 
 * 
 * @author tanfan 
 * @version  
 * @since JDK 1.7
 */
public class PoolThread extends Thread {
	//默认线程是空闲的
	private boolean idle = true;
	//默认线程不是销毁的
	private boolean shutDown = false;
	//线程的任务
	private Runnable task;
	//线程所在的线程池
	private ThreadPool pool;
	//日志组件
	private static final Logger logger = LoggerFactory.getLogger(PoolThread.class);
	
	public PoolThread(ThreadPool pool) {
		this.pool = pool;
	}
	
	public PoolThread(ThreadPool pool, Runnable task) {
		this(pool);
		this.task = task;
	}


	//工作者线程与通常线程不同之处在于run()方法的不同。通常的线程，
    //完成线程应该执行的代码后，自然退出，线程结束。
    //虚拟机在线程结束后收回分配给线程的资源，线程对象被垃圾回收。]
    //而这在池化的工作者线程中是应该避免的，否则线程池就失去了意义。
    //作为可以被放入池中并重新利用的工作者线程，它的run()方法不应该结束，
    //随意，在随后可以看到的实现中，run()方法执行完target对象的代码后，
    //就将自身repool()，然后调用wait()方法，使自己睡眠而不是退出循环和run()。
    //这就使线程池实现的要点。
	@Override
	public void run() {
		while(!shutDown){
			try{
				logger.info("线程池里的线程 PoolThread value {}",this);
				task.run();
				logger.info("执行完线程的任务");
			}catch(Exception e){
				e.printStackTrace();
			}
			idle = true;
			//回归线程池
			pool.recovery(this);
			try{
				synchronized (this) {
					this.wait();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	public boolean isIdle() {
		return idle;
	}

	public void setIdle(boolean idle) {
		this.idle = idle;
	}

	public boolean isShutDown() {
		return shutDown;
	}

	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
	}

	public Runnable getTask() {
		return task;
	}

	public void setTask(Runnable task) {
		this.task = task;
		this.notifyAll();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
