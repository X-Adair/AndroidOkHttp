package com.adair.simple;

import android.app.Application;

import com.adair.okhttp.OkHttpManager;

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
        OkHttpManager.newInstance().addParam("Session", "11111111111111").init();
    }
}
