package com.sohu.chromium.net.threadPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangyan on 2018/12/25
 */
public class ThreadPoolManager {

    private static final String TAG = "ThreadPoolManager";

    private static final long FIVE_MINIUTES = 5 * 60;

    private static final int MAX_QUEUE_SIZE = 1000;

    private static final int LOG_SENDER_CORE_SIZE = 10;

    private static final int LOG_SENDER_IDLE_SIZE = 100;

    private static final int LOG_RECEIVE_CORE_SIZE = 1;

    private ThreadPool cronetPool = null;

    private ThreadPool okhttpPool = null;

    private ThreadPool systemPool = null;

    private ExecutorService cronetExecutor = null;
    private ExecutorService okhttpExecutor = null;
    private ExecutorService systemExecutor = null;
    private ExecutorService singleExecutor = null;

    private ThreadPool singleThreadPool = null;

    private static ThreadPoolManager manager = null;

    private ThreadPoolManager() {
        initThreadPool();
    }

    public static ThreadPoolManager getInstance() {
        if (manager == null) {
            synchronized (ThreadPoolManager.class) {
                if (manager == null) {
                    manager = new ThreadPoolManager();
                }
            }
        }

        return manager;
    }

    private void initThreadPool() {
        RejectedExecutionHandler waitToAddPolicy = new Wait2AddPolicy();

        if (cronetPool == null || !cronetPool.isAlive()) {
            BlockingQueue<Runnable> logSenderQueue = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);
            cronetExecutor = new ThreadPoolExecutor(LOG_SENDER_CORE_SIZE, LOG_SENDER_IDLE_SIZE, FIVE_MINIUTES, TimeUnit.SECONDS, logSenderQueue, new ThreadFactory("CronetThreadPool"), waitToAddPolicy);
            ((ThreadPoolExecutor)cronetExecutor).allowCoreThreadTimeOut(true);
            cronetPool = new ThreadPool(cronetExecutor, "CRONET");
        }

        if (systemPool == null || !systemPool.isAlive()) {
            BlockingQueue<Runnable> logSenderQueue = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);
            systemExecutor = new ThreadPoolExecutor(LOG_SENDER_CORE_SIZE, LOG_SENDER_IDLE_SIZE, FIVE_MINIUTES, TimeUnit.SECONDS, logSenderQueue, new ThreadFactory("SystemThreadPool"), waitToAddPolicy);
            ((ThreadPoolExecutor)systemExecutor).allowCoreThreadTimeOut(true);
            systemPool = new ThreadPool(systemExecutor, "SYSTEM");
        }

        if (okhttpPool == null || !okhttpPool.isAlive()) {
            BlockingQueue<Runnable> logSenderQueue = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);
            okhttpExecutor = new ThreadPoolExecutor(LOG_SENDER_CORE_SIZE, LOG_SENDER_IDLE_SIZE, FIVE_MINIUTES, TimeUnit.SECONDS, logSenderQueue, new ThreadFactory("OkhttpThreadPool"), waitToAddPolicy);
            ((ThreadPoolExecutor)okhttpExecutor).allowCoreThreadTimeOut(true);
            okhttpPool = new ThreadPool(okhttpExecutor, "OKHTTP");
        }

        if (singleThreadPool == null || !singleThreadPool.isAlive()) {
            BlockingQueue<Runnable> singleSenderQueue = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);
            singleExecutor = new ThreadPoolExecutor(LOG_RECEIVE_CORE_SIZE, LOG_RECEIVE_CORE_SIZE, FIVE_MINIUTES, TimeUnit.SECONDS, singleSenderQueue, new ThreadFactory("MonitorSingle"), waitToAddPolicy);
            ((ThreadPoolExecutor)singleExecutor).allowCoreThreadTimeOut(true);
            singleThreadPool = new ThreadPool(singleExecutor, "SINGLE");
        }
    }

    public void addCronetTask(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        if (cronetPool != null && cronetPool.isAlive()) {
            cronetPool.execute(runnable);
        } else {
            initThreadPool();
            cronetPool.execute(runnable);
        }
    }

    public void addOkhttpTask(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        if (okhttpPool != null && okhttpPool.isAlive()) {
            okhttpPool.execute(runnable);
        } else {
            initThreadPool();
            okhttpPool.execute(runnable);
        }
    }

    public void addSystemTask(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        if (systemPool != null && systemPool.isAlive()) {
            systemPool.execute(runnable);
        } else {
            initThreadPool();
            systemPool.execute(runnable);
        }
    }

    public Executor getCronetExecutor() {
        if (cronetPool != null && cronetPool.isAlive()) {
            return cronetExecutor;
        } else {
            initThreadPool();
            return cronetExecutor;
        }
    }

    public Executor getSingleExecutor() {
        if (singleThreadPool != null && singleThreadPool.isAlive()) {
            return singleExecutor;
        } else {
            initThreadPool();
            return singleExecutor;
        }
    }
}
