package com.adair.okhttp.test;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * package：    com.adair.okhttp.test
 * author：     XuShuai
 * date：       2018/7/2  20:06
 * version:     v1.0
 * describe：
 */
public class OkHttpClientManager {

    private static OkHttpClient sDefaultOkHttpClient;

    private static HttpLoggingInterceptor sHttpLoggingInterceptor;

    private OkHttpClientManager() {
    }

    public static OkHttpClient getDefaultOkHttpClient(boolean debug) {
        if (sDefaultOkHttpClient == null) {
            synchronized (OkHttpClientManager.class) {
                if (sDefaultOkHttpClient == null) {
                    sDefaultOkHttpClient = new OkHttpClient();
                }
            }
        }
        return sDefaultOkHttpClient;
    }
}
