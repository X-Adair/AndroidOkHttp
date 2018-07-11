package com.adair.okhttp.builder;

import android.support.annotation.NonNull;

import com.adair.okhttp.OkHttp;
import com.adair.okhttp.callback.ICallback;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 基本的请求构建类
 * <p>
 * created at 2018/7/5 14:13
 *
 * @author XuShuai
 * @version v1.0
 */
public abstract class BaseRequestBuilder<T extends BaseRequestBuilder> {

    /**
     * 请求地址
     */
    protected String url;

    /**
     * 请求标记
     */
    protected Object tag;

    /**
     * 请求头
     */
    protected Map<String, String> headers;


    public BaseRequestBuilder() {
        headers = new LinkedHashMap<>();
        if (OkHttp.getCommonHeaders() != null && !OkHttp.getCommonHeaders().isEmpty()) {
            headers.putAll(OkHttp.getCommonHeaders());
        }
    }

    public T url(@NonNull String url) {
        this.url = url;
        return (T) this;
    }

    public T tag(@NonNull Object tag) {
        this.tag = tag;
        return (T) this;
    }

    public T addHeader(String key, String value) {
        headers.put(key, value);
        return (T) this;
    }

    public T headers(Map<String, String> headers) {
        this.headers = headers;
        return (T) this;
    }

    /**
     * 将头信息写入Request
     *
     * @param builder OkHttp Request builder
     * @param headers Request header map
     */
    protected void buildHeaders(Request.Builder builder, Map<String, String> headers) {
        Headers.Builder headersBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) return;

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            headersBuilder.add(entry.getKey(), entry.getValue());
        }
        builder.headers(headersBuilder.build());
    }
}
