package com.jp.nian.threadpool.core.bk;

public class PooledThread extends Thread {
	 private ThreadPool pool_;  // 池中线程需要知道自己所在的池
	    private Runnable target_;   // 线程的任务
	    private boolean shutDown_ = false;
	    private boolean idle_ = false;//设置是否让线程处于等待状态
	    
	    private PooledThread() {
	        super();
	    }
	    
	    private PooledThread(Runnable target)
	    {
	        super(target); //初始化父类
	    }
	    
	    private PooledThread(Runnable target, String name) 
	    {
	        super(target, name);
	    }
	    
	    public PooledThread(Runnable target, String name, ThreadPool pool)
	    {
	        super(name);
	        pool_ = pool;
	        target_ = target;
	    }
	    
	    private PooledThread(String name) 
	    {
	        super(name);//初始化父类
	    }
	    
	    private PooledThread(ThreadGroup group, Runnable target)
	    {
	        super(group, target);
	    }
	    
	    private PooledThread(ThreadGroup group, Runnable target, String name) 
	    {
	        super(group, target, name);
	    }
	    
	    private PooledThread(ThreadGroup group, String name) 
	    {
	        super(group, name);
	    }
	    
	    public java.lang.Runnable getTarget() 
	    {
	        return target_;
	    }
	    
	    public boolean isIdle() 
	    {
	        return idle_;//返回当前的状态
	    }
	    
	    //工作者线程与通常线程不同之处在于run()方法的不同。通常的线程，
	    //完成线程应该执行的代码后，自然退出，线程结束。
	    //虚拟机在线程结束后收回分配给线程的资源，线程对象被垃圾回收。]
	    //而这在池化的工作者线程中是应该避免的，否则线程池就失去了意义。
	    //作为可以被放入池中并重新利用的工作者线程，它的run()方法不应该结束，
	    //随意，在随后可以看到的实现中，run()方法执行完target对象的代码后，
	    //就将自身repool()，然后调用wait()方法，使自己睡眠而不是退出循环和run()。
	    //这就使线程池实现的要点。
	    public void run() 
	    {
	        // 这个循环不能结束，除非池类要求线程结束
	        // 每一次循环都会执行一次池类分配给的任务target
	        while (!shutDown_) 
	        {  
	            idle_ = false;
	            if (target_ != null) 
	            {
	                target_.run();  // 运行target中的代码
	            }
	            idle_ = true;
	            try 
	            {
	                //线程通知池重新将自己放回到池中
	                pool_.repool(this);  // 
	                //进入池中后睡眠，等待被唤醒执行新的任务，
	                //这里是线程池中线程于普通线程的run()不同的地方。
	                synchronized (this) 
	                {
	                    wait();
	                }
	            }
	            catch (InterruptedException ie)
	            {
	            }
	            idle_ = false;
	        }
	        //循环这里不能结束，否则线程结束，资源被VM收回，
	        //就无法起到线程池的作用了
	    }
	    
	    
	    public synchronized void setTarget(java.lang.Runnable newTarget) 
	    {//设置新的target，并唤醒睡眠中的线程
	        target_ = newTarget;  // 新任务
	        notifyAll();          // 唤醒睡眠的线程
	    }
	    
	    public synchronized void shutDown()
	    {
	        shutDown_ = true;
	        notifyAll();
	    }
}
