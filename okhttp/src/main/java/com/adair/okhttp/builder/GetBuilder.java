package com.adair.okhttp.builder;

import android.net.Uri;
import android.text.TextUtils;

import com.adair.okhttp.callback.ICallback;
import com.adair.okhttp.callback.OkHttpCallBack;

import java.io.IOException;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 构建以GET方式请求接口
 * created at 2018/7/9 10:48
 *
 * @author XuShuai
 * @version v1.0
 */
public class GetBuilder extends BaseRequestBuilderWithParam<GetBuilder> {

    private OkHttpClient okHttpClient;

    public GetBuilder(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    protected void buildParams(Request.Builder builder, Map<String, String> params) {
        if (params == null || params.isEmpty()) return;
        Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            uriBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        url = uriBuilder.build().toString();
    }

    /**
     * 异步执行GET请求方法
     *
     * @param callback 回调方法
     */
    public void enqueue(ICallback callback) {
        Request request = buildRequest();
        callback.onStart();
        okHttpClient.newCall(request).enqueue(new OkHttpCallBack(callback));
    }

    /**
     * 同步请求方法
     */
    public Response execute() throws IOException {
        Request request = buildRequest();
        return okHttpClient.newCall(request).execute();
    }

    /**
     * 构建Request
     *
     * @return OkHttp Request
     */
    private Request buildRequest() {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalStateException("url can not be null !");
        }
        Request.Builder builder = new Request.Builder();
        buildHeaders(builder, headers);
        buildParams(builder, params);
        builder.url(url);
        if (tag != null) {
            builder.tag(url);
        }
        return builder.build();
    }
}
