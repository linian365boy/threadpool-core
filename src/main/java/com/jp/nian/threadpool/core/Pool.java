package com.jp.nian.threadpool.core;
/**
 * @ClassName: Pool  
 * @Description: 池接口 
 * @date: 2016年10月8日 下午2:08:02 
 * 
 * @author tanfan 
 * @version  
 * @since JDK 1.7
 */
public interface Pool {
	void close();
	void execute(Runnable task);
}
