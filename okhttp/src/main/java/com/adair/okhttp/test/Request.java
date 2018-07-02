package com.adair.okhttp.test;

import java.util.HashMap;

import okhttp3.OkHttpClient;

/**
 * package：    com.adair.okhttp.test
 * author：     XuShuai
 * date：       2018/7/2  20:35
 * version:     v1.0
 * describe：
 */
public class Request {
    protected OkHttpClient mOkHttpClient;

    protected HashMap<String, String> mHeaderMap;

    protected HashMap<String, String> mParamMap;

    public Request(OkHttpClient okHttpClient) {
        mOkHttpClient = okHttpClient;
        mHeaderMap = new HashMap<>();
        mParamMap = new HashMap<>();
    }

    public Request addHeader(String key, String value) {
        mHeaderMap.put(key, value);
        return this;
    }

    public Request addParam(String key, String value) {
        mParamMap.put(key, value);
        return this;
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        mOkHttpClient = okHttpClient;
    }
}
