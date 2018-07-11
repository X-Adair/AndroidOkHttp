package com.adair.okhttp.callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * 回调接口
 * <p>
 * created at 2018/7/2 13:46
 *
 * @author XuShuai
 * @version v1.0
 */
public interface ICallback {

    /**
     * 请求开始前调用,此方法必定会被执行
     */
    void onStart();

    /**
     * 请求错误回调接口(请求服务器无响应)
     *
     * @param e 错误Exception
     */
     void onFailure(IOException e);

    /**
     * 请求响应成功(请求服务器有响应)
     *
     * @param code     响应code
     * @param response 响应结果
     */
    void onSuccess(int code, Response response);

    /**
     * 请求结束,此方法必定会被执行
     */
    void onFinish();
}
