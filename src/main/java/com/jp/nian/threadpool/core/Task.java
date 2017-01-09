package com.jp.nian.threadpool.core;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Task {
	private static final Logger logger = LoggerFactory.getLogger(Task.class);
	private String name;

	public Task(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "Task[name="+name+"]";
	}

	public void doSomething() {
		long sleepSecond = new Double(Math.random()*10).longValue();
		try {
			logger.info("I am doing my task, I need sleep {} s", sleepSecond);
			TimeUnit.SECONDS.sleep(sleepSecond);
		} catch (InterruptedException e) {
			logger.error("I am sleep and happend error ", e);
		}
		logger.info("I am done my thing ...");
	}
}
