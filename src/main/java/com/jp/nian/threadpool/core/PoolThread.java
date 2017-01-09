package com.jp.nian.threadpool.core;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: PoolThread  
 * @Description: 线程池中的线程 
 * 工作者线程与通常线程不同之处在于run()方法的不同。通常的线程，
    完成线程应该执行的代码后，自然退出，线程结束。
    虚拟机在线程结束后收回分配给线程的资源，线程对象被垃圾回收。]
    而这在池化的工作者线程中是应该避免的，否则线程池就失去了意义。
    作为可以被放入池中并重新利用的工作者线程，它的run()方法不应该结束，
    随意，在随后可以看到的实现中，run()方法执行完任务后，
    就将回收到池中，然后调用wait()方法，使自己等待而不是退出循环和run()。
    这就使线程池实现的要点。
 * @date: 2016年10月8日 下午2:23:07 
 * 
 * @author tanfan 
 * @version  
 * @since JDK 1.7
 */
public class PoolThread extends Thread {
	//线程的任务
	private Task task;
	//线程所在的线程池
	private ThreadPool pool;
	private ThreadLocal<Boolean> idleLocal = new ThreadLocal<Boolean>(){
		protected Boolean initialValue(){
			return true;
		}
	};
	
	//日志组件
	private static final Logger logger = LoggerFactory.getLogger(PoolThread.class);
	
	public PoolThread(ThreadPool pool) {
		this.pool = pool;
	}
	
	@Override
	public void run() {
		while(idleLocal.get()){
			if(task!=null){
				try{
					synchronized (this) {
						logger.info("{} will be execute task", this.getName());
						idleLocal.set(false);
						task.doSomething();
						//归还这个Thread
						pool.returnToPool(this);
						logger.info("{} do the work and will be wait", this.getName());
						this.wait();
						logger.info("{} is notifyed by other ", this.getName());
						if(pool.isShutdown()){
							break;
						}
						idleLocal.set(true);
					}
				}catch(Exception e){
					logger.error("thread task after error",e);
				}
			}
		}
	}
	
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		logger.info("I set the task now ...");
		this.task = task;
		synchronized (this) {
			logger.info("I enter set the task synchronized ...");
			this.notifyAll();
		}
	}
	
	
	public ThreadLocal<Boolean> getIdleLocal() {
		return idleLocal;
	}

	public void setIdleLocal(ThreadLocal<Boolean> idleLocal) {
		this.idleLocal = idleLocal;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public void close() {
		//logger.info("线程池正在关闭={}",pool.isShutdown());
		synchronized (this) {
			this.notify();
		}
	}
}
