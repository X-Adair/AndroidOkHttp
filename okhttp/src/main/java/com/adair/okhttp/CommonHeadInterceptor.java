package com.adair.okhttp;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * okHttp拦截器，此拦截器用于添加公共的请求头信息
 * <p>
 * created at 2018/6/19 11:09
 *
 * @author XuShuai
 * @version v1.0
 */
public class CommonHeadInterceptor implements Interceptor {

    private Map<String, String> mHeadMap;

    public CommonHeadInterceptor(Map<String, String> headMap) {
        this.mHeadMap = headMap;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request().newBuilder();

        if (mHeadMap != null && !mHeadMap.isEmpty()) {
            for (Map.Entry<String, String> entry : mHeadMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.build();
        return chain.proceed(request);
    }
}
