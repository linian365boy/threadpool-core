package com.jp.nian.threadpool.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Task implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(Task.class);
	private String name;

	public Task(String name) {
		this.name = name;
	}
	
	@Override
	public void run() {
		logger.info("Task value{}",this);
	}
	
	@Override
	public String toString() {
		return "Task[name="+name+"]";
	}
}
