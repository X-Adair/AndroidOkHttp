package com.adair.okhttp;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 上传下载线程池工具类
 * created at 2018/6/29 9:35
 *
 * @author XuShuai
 * @version v1.0
 */
public class ThreadPoolManager {
    private static volatile ThreadPoolManager ourInstance;

    private static final int DEFAULT_CORE_POOL_SIZE = 5;

    private static ThreadPoolExecutor sExecutor;


    public static ThreadPoolManager getInstance() {
        if (ourInstance == null) {
            synchronized (ThreadPoolManager.class) {
                if (ourInstance == null) {
                    ourInstance = new ThreadPoolManager();
                }
            }
        }
        return ourInstance;
    }

    /**
     * 初始化时创建线程池,支持最多5个任务同时执行
     */
    private ThreadPoolManager() {
        init();
    }

    private void init() {
        if (sExecutor == null) {
            sExecutor = new ThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE, DEFAULT_CORE_POOL_SIZE, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
        }
    }

    /**
     * 设置核心线程数
     *
     * @param corePoolSize 核心线程数量
     */
    public void setCorePoolSize(int corePoolSize) {
        if (sExecutor == null) {
            sExecutor = new ThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE, DEFAULT_CORE_POOL_SIZE, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
        } else {
            sExecutor.setMaximumPoolSize(corePoolSize);
            sExecutor.setCorePoolSize(corePoolSize);
        }
    }


    /**
     * 执行任务
     *
     * @param runnable 线程任务
     */
    public void executor(Runnable runnable) {
        if (sExecutor == null) {
            init();
        }
        sExecutor.execute(runnable);
    }

    /**
     * 移除任务
     *
     * @param runnable 线程任务
     */
    public void remove(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (sExecutor != null) {
            sExecutor.remove(runnable);
        }
    }
}
