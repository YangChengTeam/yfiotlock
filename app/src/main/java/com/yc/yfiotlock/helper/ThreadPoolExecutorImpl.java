package com.yc.yfiotlock.helper;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/13
 **/
public class ThreadPoolExecutorImpl {

    private static ThreadPoolExecutor mThreadPoolExecutor;
    /**
     * 核心线程
     */
    private final static int CORE_POOL_SIZE = 3;

    /**
     * 最大线程 当前需要的线程数量超过核心线程数量的时候会创建新的线程
     */
    private final static int MAX_POOL_SIZE = 6;

    /**
     * ArrayBlockingQueue：基于数组的先进先出队列，此队列创建时必须指定大小；
     * <p>
     * LinkedBlockingQueue：基于链表的先进先出队列，如果创建时没有指定此队列大小，则默认为Integer.MAX_VALUE；
     * <p>
     * synchronousQueue：这个队列比较特殊，它不会保存提交的任务，而是将直接新建一个线程来执行新来的任务。
     *
     * @return instance of this class
     */
    public static ThreadPoolExecutor getImpl() {
        if (mThreadPoolExecutor == null) {
            synchronized (ThreadPoolExecutorImpl.class) {
                if (mThreadPoolExecutor == null) {
                    mThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                            0L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<>(), new MyThreadFactory());
                }
            }
        }
        return mThreadPoolExecutor;
    }

    private static class MyThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            SecurityManager s = System.getSecurityManager();
            ThreadGroup group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            Thread t = new Thread(group, r,
                    "YC-thread-pool-" + new AtomicInteger(1).getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
