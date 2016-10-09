package com.jp.nian.threadpool.core;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMain {
	private static final Logger logger = LoggerFactory.getLogger(TestMain.class);
	public static void main(String[] args) {
		logger.info("start....");
		//初始化10个线程
		Pool pool = new ThreadPool(10);
		//初始化20个任务
		Task[] task = new Task[20];
		for(int i=0;i < task.length;i++){
			task[i] = new Task("name"+i);
			pool.execute(task[i]);
		}
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(pool!=null){
			pool.close();
		}
		logger.info("线程池关闭....");
	}
}
