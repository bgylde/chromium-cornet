package com.sohu.chromium.net.utils.threadPool;

import java.util.concurrent.ExecutorService;

/**
 * Created by wangyan on 2018/12/25
 */
class ThreadPool {
    private static final String TAG = "ThreadPool";

    private String poolName;

    private ExecutorService executor = null;

    ThreadPool (ExecutorService executor, String poolName) {
        this.executor = executor;
        this.poolName = poolName;
    }

    protected void execute(Runnable runnable) {
        if (executor != null) {
            //LogMonitor.i(TAG, poolName + " add thread pool runnable: " + runnable);
            executor.execute(runnable);
            //LogMonitor.i(TAG, poolName + " active count: " + ((ThreadPoolExecutor)executor).getActiveCount());
            //LogMonitor.i(TAG, poolName + " waiting count: " + ((ThreadPoolExecutor)executor).getQueue().size());
            //LogMonitor.i(TAG, poolName + " completed count: " + ((ThreadPoolExecutor)executor).getCompletedTaskCount());
        } else {
            throw new RuntimeException("Thread pool execute is null, error!");
        }
    }

    protected boolean isAlive() {
        return executor != null && !executor.isShutdown() && !executor.isTerminated();
    }

    protected void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
    }
}
