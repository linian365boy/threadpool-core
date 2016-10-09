package com.jp.nian.threadpool.core;

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
public class ThreadPool implements Pool {
	//默认线程池的线程数
	private static int default_size = 5;
	//线程池中的空闲线程
	private PoolThread[] idleThreads;
	private boolean shudown = false;
	//日志组件
	private static final Logger logger = LoggerFactory.getLogger(ThreadPool.class);
	
	public ThreadPool(){
		this(default_size);
	}
	
	public ThreadPool(int threadSize){
		ThreadPool.default_size = threadSize;
		this.idleThreads = new PoolThread[threadSize];
		/*for(int i = 0; i < default_size; i++){
			idleThreads[i] = new PoolThread(this);
		}*/
	}
	
	
	
	@Override
	public void close() {
		shudown = true;
		for(int i=0;i<idleThreads.length;i++){
			idleThreads[i].setShutDown(true);
			idleThreads[i].interrupt();
		}
	}

	@Override
	public void execute(Runnable task) {
		for(int i=0;i<idleThreads.length;i++){
			if(idleThreads[i]==null){
				idleThreads[i] = new PoolThread(this);
				idleThreads[i].start();
			}
			if(idleThreads[i].isIdle()){
				idleThreads[i].setTask(task);
				idleThreads[i].start();
				idleThreads[i].setIdle(false);
				break;
			}
		}
	}
	
	/**
	 * recovery:线程完成任务，回收线程池的线程
	 * @author tanfan 
	 * @param poolThread 
	 * @since JDK 1.7
	 */
	public void recovery(PoolThread poolThread) {
		if(!shudown){
			logger.info("线程池PoolThread value {}",poolThread);
			poolThread.setIdle(true);
		}
	}
	
	/**
	 * getIdleThread:拿线程池的空闲线程
	 * @author tanfan 
	 * @return 
	 * @since JDK 1.7
	 */
	public PoolThread[] getIdleThread() {
		int m = 0;
		for(int i=0;i < idleThreads.length;i++){
			if(idleThreads[i].isIdle()){
				idleThreads[m++] = idleThreads[i];
			}
		}
		return idleThreads;
	}

}
