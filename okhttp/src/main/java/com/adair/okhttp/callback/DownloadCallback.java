package com.adair.okhttp.callback;

import java.io.IOException;

/**
 * created at 2018/7/10 14:18
 *
 * @author XuShuai
 * @version v1.0
 */
public abstract class DownloadCallback {

    /**
     * 下载开始
     */
    public void onStart() {

    }

    /**
     * 下载结束
     */
    public void onFinish() {

    }

    /**
     * 下载成功
     *
     * @param path 保存文件路径
     */
    public abstract void onSuccess(String path);

    /**
     * 下载进度
     *
     * @param currentBytes 当前下载长度
     * @param totalBytes   文件总长度
     */
    public abstract void onProgress(long currentBytes, long totalBytes);

    /**
     * 下载错误
     *
     * @param e 错误信息
     */
    public abstract void onFail(IOException e);
}
