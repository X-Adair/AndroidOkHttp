package com.adair.simple;

import android.app.Application;

import com.adair.okhttp.OkHttp;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * created at 2018/6/19 15:55
 *
 * @author XuShuai
 * @version v1.0
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttp.init()
                .addCommonHeader("session", "11akaskfkljfljafa")
                .addCommonParams("WD", "CSDN")
//                .addInterceptor(interceptor)
                .build();
    }
}
