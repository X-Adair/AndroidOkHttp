package com.adair.okhttp.builder;

import android.text.TextUtils;

import com.adair.okhttp.callback.ICallback;
import com.adair.okhttp.callback.OkHttpCallBack;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 构建POST方式请求接口
 * <p>
 * created at 2018/7/9 13:52
 *
 * @author XuShuai
 * @version v1.0
 */
public class PostBuilder extends BaseRequestBuilderWithParam<PostBuilder> {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    private OkHttpClient okHttpClient;

    //是否以json字符串的方式传递参数,默认false
    private boolean isJsonPost = false;

    private Gson gson;

    public PostBuilder(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    /**
     * 以JSON 字符串法人方式传递参数
     *
     * @return this
     */
    public PostBuilder json() {
        if (gson == null) {
            gson = new Gson();
        }
        isJsonPost = true;
        return this;
    }

    @Override
    protected void buildParams(Request.Builder builder, Map<String, String> params) {
        if (isJsonPost) {
            String jsonString = gson.toJson(params);
            RequestBody requestBody = RequestBody.create(JSON, jsonString);
            builder.post(requestBody);
        } else {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formBodyBuilder.add(entry.getKey(), entry.getValue());
            }
            builder.post(formBodyBuilder.build());
        }
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

    //构建Request
    private Request buildRequest() {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalStateException("url can not be null !");
        }
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        buildHeaders(builder, headers);
        if (tag != null) {
            builder.tag(tag);
        }
        buildParams(builder, params);
        return builder.build();
    }
}
