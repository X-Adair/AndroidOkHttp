package com.adair.okhttp.interceptor;

import com.adair.okhttp.callback.DownloadCallback;
import com.adair.okhttp.response.DownloadResponseBody;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * created at 2018/7/11 10:06
 *
 * @author XuShuai
 * @version v1.0
 */
public class DownloadInterceptor implements Interceptor {

    private DownloadCallback callback;

    public DownloadInterceptor(DownloadCallback callback) {
        this.callback = callback;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder().body(new DownloadResponseBody(response.body(), callback)).build();
    }
}
