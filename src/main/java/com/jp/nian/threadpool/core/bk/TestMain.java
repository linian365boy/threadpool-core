package com.jp.nian.threadpool.core.bk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMain {
	private static final Logger logger = LoggerFactory.getLogger(TestMain.class);
	public static void main(String[] args) {
        ThreadPool pool = ThreadPool.instance();
        pool.setDebug(true);
        class TestRunner implements Runnable 
        {
            public String name;
            public TestRunner(String name){
            	this.name = name;
            }
            public void run() 
            {
            	logger.info("This is the task name "+name);
            }
        }
        for(int i=0;i<30;i++){
        	pool.start(new TestRunner("name"+i),ThreadPool.HIGH_PRIORITY);
        }
        logger.info("Thread count : " + pool.getCreatedThreadsCount());
        pool.shutdown();
	}
}
