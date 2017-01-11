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
		while(idleLocal!=null && idleLocal.get()){
			logger.debug("{} my task is |{}", this.getName(), task);
			if(task!=null){
				try{
					idleLocal.set(false);
					logger.debug("{} will be execute task", this.getName());
					task.doSomething();
					synchronized (this) {
						//归还这个Thread
						pool.returnToPool(this);
						if(pool.isShutdown()){
							logger.debug("find thread pool shutdown, I am going die......");
							break;
						}
						logger.debug("{} do the work and will be wait", this.getName());
						this.wait();
						logger.debug("{} is notifyed by other ", this.getName());
					}
					if(idleLocal!=null){
						idleLocal.set(true);
					}
				}catch(Exception e){
					logger.error("thread|{} execute task error",this.getName(), e);
				}
			}
		}
	}
	
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		logger.debug("{} set the task|{} now ...",this.getName(), task);
		this.task = task;
		synchronized (this) {
			logger.debug("{} set the task synchronized ...", this.getName());
			this.notifyAll();
		}
	}
	
	public ThreadLocal<Boolean> getIdleLocal() {
		return idleLocal;
	}

	public void setIdleLocal(ThreadLocal<Boolean> idleLocal) {
		this.idleLocal = idleLocal;
	}

	public void close() {
		synchronized (this) {
			this.notifyAll();
		}
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
