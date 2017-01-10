package com.jp.nian.threadpool.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMain {
	private static final Logger logger = LoggerFactory.getLogger(TestMain.class);
	public static void main(String[] args) {
		logger.info("start....");
		//初始化10个线程，默认5个初始化线程
		Pool<PoolThread> pool = new ThreadPool(10);
		//初始化20个任务
		Task[] task = new Task[20];
		for(int i=0;i < task.length;i++){
			task[i] = new Task("name"+i);
			//10个线程去执行20个任务，肯定有线程执行2个或2个以上的任务
			pool.execute(task[i]);
		}
		//等待任务运行完执行
		pool.shutdown();
		//pool.shutdownnow();
		logger.info("线程池关闭....");
	}
}
