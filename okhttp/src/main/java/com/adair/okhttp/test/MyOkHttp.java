package com.adair.okhttp.test;

import com.adair.okhttp.OkHttpRequest;

import java.util.Map;

/**
 * 请求入口
 * <p>
 * created at 2018/7/2 14:04
 *
 * @author XuShuai
 * @version v1.0
 */
public class MyOkHttp {




    private MyOkHttp() {
    }

    public static GetRequest get() {
        return new GetRequest();
    }


}
