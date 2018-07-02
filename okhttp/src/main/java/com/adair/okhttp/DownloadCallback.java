package com.adair.okhttp;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 下载文件回调
 * <p>
 * created at 2018/6/29 14:08
 *
 * @author XuShuai
 * @version v1.0
 */
public abstract class DownloadCallback {

    /**
     * 请求之前的回调
     * <p>
     * created at 2018/6/20 10:47
     */
    public void onStart() {
    }

    /**
     * 请求之后的回调，无论是否请求成功
     * <p>
     * created at 2018/6/20 10:48
     */
    public void onEnd() {
    }

    /**
     * 请求成功回调
     * <p>
     * created at 2018/6/20 10:50
     *
     * @param s 回调结果
     */
    public abstract void onSuccess(String s);

    /**
     * 下载进度
     *
     * @param downloadLength 已下载长度
     * @param totalLength    文件总长度
     */
    public abstract void onProgress(long downloadLength, long totalLength);


    /**
     * 当请求成功，但是code不为200时，调用此接口
     * <p>
     * created at 2018/6/20 10:51
     *
     * @param code     http返回code
     * @param response 请求response
     */
    public abstract void onError(int code, Response response);

    /**
     * 请求失败
     * <p>
     * created at 2018/6/21 15:13
     */
    public void onFailure(Call call, IOException e) {
    }
}
