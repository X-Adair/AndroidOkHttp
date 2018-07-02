package com.adair.okhttp.test;

import com.adair.okhttp.Callback;
import com.adair.okhttp.OkHttpManager;

import java.util.Map;

/**
 * get请求
 * <p>
 * created at 2018/7/2 14:06
 *
 * @author XuShuai
 * @version v1.0
 */
public class GetRequest {

    /**
     * 参数Map
     */
    private Map<String, String> paramsMap;

    /**
     * 请求头Map
     */
    private Map<String, String> headerMap;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 请求标记
     */
    private String tag;

    /**
     * 请求地址
     *
     * @param url 请求地址
     */
    public GetRequest url(String url) {
        this.url = url;
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param key   参数名称
     * @param value 参数值
     */
    public GetRequest addParam(String key, String value) {
        paramsMap.put(key, value);
        return this;
    }

    /**
     * 添加头部信息
     *
     * @param key   头部信息key
     * @param value 头部信息值
     */
    public GetRequest addHeader(String key, String value) {
        headerMap.put(key, value);
        return this;
    }

    /**
     * 请求标志
     *
     * @param tag okHttp tag
     */
    public GetRequest tag(String tag) {
        this.tag = tag;
        return this;
    }

    /**
     * 开始请求
     *
     * @param callback 请求回调
     */
    public void enqueue(Callback callback) {
//        OkHttpManager.newInstance().enqueue(buildRequest(), callback);
    }
}
