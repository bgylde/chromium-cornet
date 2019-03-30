package com.sohu.chromium.net.utils.threadPool;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangyan on 2019/1/30
 */
public class ThreadFactory implements java.util.concurrent.ThreadFactory {

    private static String TAG = "MontorThreadFactory";

    private final ThreadGroup group;
    private String threadName;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    ThreadFactory(String threadName) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        this.threadName = threadName;
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                threadName + "--" + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
