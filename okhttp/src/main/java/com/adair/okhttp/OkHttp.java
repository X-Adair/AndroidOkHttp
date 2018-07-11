package com.adair.okhttp;


import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.adair.okhttp.builder.DownloadBuilder;
import com.adair.okhttp.builder.GetBuilder;
import com.adair.okhttp.builder.PostBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * created at 2018/7/3 14:43
 *
 * @author XuShuai
 * @version v1.0
 */
public class OkHttp {


    private static boolean isInitialize = false;

    private static OkHttpClient okHttpClient;
    private static Handler uiHandler;
    private static Map<String, String> commonHeaders;
    private static Map<String, String> commonParams;

    private static List<Call> downloadCall;

    private OkHttp(Builder builder) {
        okHttpClient = builder.okHttpClient;
        commonHeaders = builder.headers;
        commonParams = builder.params;
        isInitialize = true;
    }

    /**
     * 初始化,返回Builder方便链式调用方法
     */
    public static Builder init() {
        uiHandler = new Handler(Looper.getMainLooper());
        return new OkHttp.Builder();
    }

    /**
     * get  handler on UI Thread
     */
    public static Handler getUiHandler() {
        return uiHandler;
    }

    public static Map<String, String> getCommonHeaders() {
        return commonHeaders;
    }


    public static Map<String, String> getCommonParams() {
        return commonParams;
    }

    public static void addCommonParams(String key, String value) {
        if (commonParams == null) {
            commonParams = new LinkedHashMap<>();
        }
        commonParams.put(key, value);
    }

    public static void commonParams(Map<String, String> params) {
        commonParams = params;
    }

    public static void addCommonHeader(String key, String value) {
        if (commonHeaders == null) {
            commonHeaders = new LinkedHashMap<>();
        }
        commonHeaders.put(key, value);
    }

    public static void commonHeader(Map<String, String> headers) {
        commonHeaders = headers;
    }

    public static void addDownloadCall(Call call) {
        if (downloadCall == null) {
            downloadCall = new ArrayList<>();
        }
        downloadCall.add(call);
    }


    /**
     * 调用Get请求
     */
    public static GetBuilder get() {
        checkOkHttpInit();
        return new GetBuilder(okHttpClient);

    }

    /**
     * 调用Post请求
     */
    public static PostBuilder post() {
        checkOkHttpInit();
        return new PostBuilder(okHttpClient);
    }

    /**
     * 下载请求
     */
    public static DownloadBuilder download() {
        checkOkHttpInit();
        return new DownloadBuilder(okHttpClient);
    }


    //检测OkHttp是否初始化
    private static void checkOkHttpInit() {
        if (!isInitialize) {
            throw new IllegalArgumentException("Please initialize OKHttp");
        }
    }

    /**
     * 关闭请求
     */
    public static void cancel(Object tag) {
        if (downloadCall != null) {
            for (Call call : downloadCall) {
                if (tag.equals(call.request().tag())) {
                    if (!call.isCanceled()) {
                        call.cancel();
                    }
                    downloadCall.remove(call);
                }
            }
        }

        Dispatcher dispatcher = okHttpClient.dispatcher();
        for (Call call : dispatcher.queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : dispatcher.runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }


    /**
     * okHttp 构建
     */
    public static class Builder {

        private OkHttpClient okHttpClient;
        private OkHttpClient.Builder okHttpClientBuilder;
        private Map<String, String> headers;
        private Map<String, String> params;

        Builder() {
            okHttpClientBuilder = new OkHttpClient.Builder();
            okHttpClientBuilder.connectTimeout(10000L, TimeUnit.MILLISECONDS)
                               .writeTimeout(10000L, TimeUnit.MILLISECONDS)
                               .readTimeout(10000L, TimeUnit.MILLISECONDS);
        }

        public Builder connectTimeout(long timeout, TimeUnit unit) {
            okHttpClientBuilder.connectTimeout(timeout, unit);
            return this;
        }

        public Builder writeTimeout(long timeout, TimeUnit unit) {
            okHttpClientBuilder.writeTimeout(timeout, unit);
            return this;
        }

        public Builder readTimeout(long timeout, TimeUnit unit) {
            okHttpClientBuilder.readTimeout(timeout, unit);
            return this;
        }

        public Builder addNetWorkInterceptor(Interceptor interceptor) {
            okHttpClientBuilder.addNetworkInterceptor(interceptor);
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            okHttpClientBuilder.addInterceptor(interceptor);
            return this;
        }

        public Builder CookieJar(@NonNull CookieJar cookieJar) {
            okHttpClientBuilder.cookieJar(cookieJar);
            return this;
        }

        public Builder addCommonHeader(String key, String value) {
            if (headers == null) {
                headers = new LinkedHashMap<>();
            }
            headers.put(key, value);
            return this;
        }

        public Builder addCommonParams(String key, String value) {
            if (params == null) {
                params = new LinkedHashMap<>();
            }
            params.put(key, value);
            return this;
        }

        public void build() {
            okHttpClient = okHttpClientBuilder.build();
            new OkHttp(this);
        }
    }
}
