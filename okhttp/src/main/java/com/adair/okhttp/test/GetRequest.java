package com.adair.okhttp.test;

import okhttp3.OkHttpClient;

/**
 * get请求
 * <p>
 * created at 2018/7/2 14:06
 *
 * @author XuShuai
 * @version v1.0
 */
public class GetRequest extends Request {

    public GetRequest(OkHttpClient okHttpClient) {
        super(okHttpClient);
    }
}
