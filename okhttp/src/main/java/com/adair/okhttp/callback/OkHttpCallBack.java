package com.adair.okhttp.callback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * okHttp 请求回调接口实现
 * <p>
 * created at 2018/7/3 14:35
 *
 * @author XuShuai
 * @version v1.0
 */
public class OkHttpCallBack implements okhttp3.Callback {

    private ICallback callback;

    public OkHttpCallBack(ICallback callback) {
        this.callback = callback;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        callback.onFailure(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        int code = response.code();
        callback.onSuccess(code, response);
    }
}
