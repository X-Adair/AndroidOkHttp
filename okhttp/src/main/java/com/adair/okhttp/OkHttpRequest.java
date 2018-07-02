package com.adair.okhttp;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * OkHttp 请求对象体封装
 * <p>
 * created at 2018/6/20 11:02
 *
 * @author XuShuai
 * @version v1.0
 */
public final class OkHttpRequest {
    /**
     * 构造模式
     */
    private Builder builder;

    private OkHttpRequest() {
    }

    /**
     * 私有初始化，只能通过Builder.build()方法获取实例对象
     * <p>
     * created at 2018/6/20 11:17
     */
    private OkHttpRequest(Builder builder) {
        this.builder = builder;
    }

    /**
     * 创建OkHttp Request请求体
     * <p>
     * created at 2018/6/21 11:27
     *
     * @return okhttp3.Request
     */
    private Request buildRequest() {
        Request.Builder requestBuilder = new Request.Builder();
        addHeader(requestBuilder);
        switch (builder.method) {
            case "GET":
                requestBuilder.url(buildGetRequestParam());
                requestBuilder.get();
                break;
            case "POST":
                requestBuilder.url(builder.url);
                try {
                    requestBuilder.post(buildPostRequestParam());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        return requestBuilder.build();
    }

    /**
     * 在Builder中添加头信息
     * <p>
     * created at 2018/6/21 14:16
     *
     * @param requestBuilder Request.Builder
     */
    private void addHeader(Request.Builder requestBuilder) {
        if (!builder.headerMap.isEmpty()) {
            for (Map.Entry<String, String> entry : builder.headerMap.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 构建Get请求参数，返回新的请求地址
     * <p>
     * created at 2018/6/21 13:53
     *
     * @return java.lang.String
     */
    private String buildGetRequestParam() {
        if (builder.paramsMap.isEmpty()) {
            return builder.url;
        }

        Uri.Builder urlBuilder = Uri.parse(builder.url).buildUpon();
        for (Map.Entry<String, String> entry : builder.paramsMap.entrySet()) {
            urlBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return urlBuilder.build().toString();
    }

    /**
     * 返回RequestBody，分为json传递参数和Form传递参数
     * <p>
     * created at 2018/6/21 14:11
     *
     * @return okhttp3.RequestBody
     */
    private RequestBody buildPostRequestParam() throws JSONException {
        if (builder.isJsonPost) {
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, String> entry : builder.paramsMap.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }

            String jsonString = jsonObject.toString();
            return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
        }

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : builder.paramsMap.entrySet()) {
            formBodyBuilder.add(entry.getKey(), entry.getValue());
        }
        return formBodyBuilder.build();
    }

    /**
     * 开始请求
     * <p>
     * created at 2018/6/21 15:10
     */
    public void enqueue(Callback callback) {
        OkHttpManager.newInstance().enqueue(buildRequest(), callback);
    }


    /**
     * 构建者模式Builder类
     * <p>
     * created at 2018/6/21 14:27
     *
     * @author XuShuai
     * @version v1.0
     */
    public static class Builder {
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
         * GET,POST
         */
        private String method;

        /**
         * 请求标记
         */
        private String tag;

        /**
         * 是否使用json类型传递参数,默认使用json传递参数
         */
        private boolean isJsonPost;


        public Builder() {
            paramsMap = new HashMap<>();
            headerMap = new HashMap<>();
            method = "GET";
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder addParam(String key, String value) {
            paramsMap.put(key, value);
            return this;
        }

        public Builder addHeader(String key, String value) {
            headerMap.put(key, value);
            return this;
        }

        public Builder get() {
            method = "GET";
            return this;
        }

        public Builder post() {
            method = "POST";
            isJsonPost = true;
            return this;
        }

        public Builder post(boolean isJsonPost) {
            method = "POST";
            this.isJsonPost = isJsonPost;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public OkHttpRequest build() {
            return new OkHttpRequest(this);
        }
    }

}
