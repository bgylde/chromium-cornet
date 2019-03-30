package com.sohu.chromium.net.utils.threadPool;

import com.sohu.chromium.net.utils.LogUtils;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by wangyan on 2018/12/25
 */
class Wait2AddPolicy implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        LogUtils.w("Thread pool is full or has been shutdown, runnable[" + r + "] is rejected by " + executor);
    }
}
