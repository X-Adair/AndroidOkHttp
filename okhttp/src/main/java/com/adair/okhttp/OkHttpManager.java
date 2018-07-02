package com.adair.okhttp;

import android.os.Handler;
import android.os.Looper;
import com.google.gson.Gson;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * okHttpClient工厂类,在Application中使用Builder类进行初始化
 * created at 2018/6/19 10:56
 *
 * @author XuShuai
 * @version v1.0
 */
public class OkHttpManager {

    private static final String TAG = "OkHttpManager";

    /**
     * 默认连接时间
     */
    private static final int CONNECT_TIME_OUT = 15;

    /**
     * 默认写入超时时间
     */
    private static final int WRITE_TIME_OUT = 15;

    /**
     * 默认读取超时时间
     */
    private static final int READ_TIME_OUT = 15;

    /**
     * OkHttpManager 单例模式对象
     */
    private static volatile OkHttpManager INSTANCE;

    /**
     * OkHttpClient对象
     */
    private static OkHttpClient okHttpClient;

    /**
     * 公共参数map
     */
    private Map<String, String> paramMap = new HashMap<>();

    /**
     * 公共请求头部Map
     */
    private Map<String, String> headMap = new HashMap<>();

    /**
     * 连接超时时间
     */
    private int connectTimeOut = CONNECT_TIME_OUT;

    /**
     * 写入超时时间
     */
    private int writeTimeOut = WRITE_TIME_OUT;

    /**
     * 读取超时时间
     */
    private int readTimeOut = READ_TIME_OUT;

    /**
     * 是否打印OkHttp Log
     */
    private boolean hasLog;


    private Handler mUiHandler;

    private Gson mGson;

    /**
     * 私有OkHttpFactory实例化方法，防止直接被new出对象
     */
    private OkHttpManager() {
    }

    /**
     * 单列模式实现
     * <p>
     * created at 2018/6/19 16:51
     *
     * @return com.adair.okhttp.OkHttpManager
     */
    public static OkHttpManager newInstance() {
        if (INSTANCE == null) {
            synchronized (OkHttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new OkHttpManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化的时候添加公共Header
     *
     * @param key    header name
     * @param header header value
     * @return OkHttpManager
     */
    public OkHttpManager addHeader(String key, String header) {
        headMap.put(key, header);
        return this;
    }

    /**
     * 初始化添加公共参数，每个请求都会带有此参数
     * <p>
     * created at 2018/6/19 17:06
     *
     * @param key   参数名
     * @param param 参数value
     * @return com.adair.okhttp.OkHttpManager
     */
    public OkHttpManager addParam(String key, String param) {
        paramMap.put(key, param);
        return this;
    }

    /**
     * 设置连接超时时间
     * <p>
     * created at 2018/6/19 17:16
     *
     * @param connectTimeOut 超时时间
     */
    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    /**
     * 设置写入超时
     * <p>
     * created at 2018/6/19 17:16
     *
     * @param writeTimeOut 写入超时时间
     */
    public void setWriteTimeOut(int writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
    }

    /**
     * 设置读取超时时间
     * <p>
     * created at 2018/6/19 17:16
     *
     * @param readTimeOut 读取超时时间
     */
    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    /**
     * 初始化OkHttpClient
     */
    public void init() {

        mUiHandler = new Handler(Looper.myLooper());
        mGson = new Gson();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connectTimeOut, TimeUnit.SECONDS);
        builder.writeTimeout(writeTimeOut, TimeUnit.SECONDS);
        builder.readTimeout(readTimeOut, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        builder.followRedirects(true);
        if (headMap != null && !headMap.isEmpty()) {
            builder.addInterceptor(new CommonHeadInterceptor(headMap));
        }
        if (hasLog) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        okHttpClient = builder.build();
    }

    /**
     * 获取OkHttpClient对象
     * <p>
     * created at 2018/6/19 17:08
     *
     * @return okhttp3.OkHttpClient
     */
    public static OkHttpClient okHttpClient() {
        if (okHttpClient == null) {
            throw new RuntimeException("you do not init OKHttpFactory");
        }
        return okHttpClient;
    }

    /**
     * okHttp请求网络并处理回调方法
     *
     * @param request  OkHttp Request
     * @param callback 自定义的请求回调方法
     */
    public void enqueue(Request request, final Callback callback) {
        callback.onStart();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendOnFailureMessage(callback, call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();
                if (code == 200) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String result = body.string();
                        if (callback.type == null || callback.type == String.class) {
                            sendOnSuccessMessage(callback, result);
                        } else {
                            sendOnSuccessMessage(callback, mGson.fromJson(result, callback.type));
                        }
                        body.close();
                    }
                } else {
                    sendOnErrorMessage(callback, code, response);
                }
            }
        });
    }

    /**
     * 在Ui线程调用失败回调方法
     *
     * @param callback 回调
     * @param call     OkHttp Call
     * @param e        Exception
     */
    private void sendOnFailureMessage(final Callback callback, final Call call, final IOException e) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(call, e);
                callback.onEnd();
            }
        });
    }

    /**
     * 在UI线程调用成功回调方法
     *
     * @param callback 回调类
     * @param o        返回解析对象
     */
    private void sendOnSuccessMessage(final Callback callback, final Object o) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(o);
                callback.onEnd();
            }
        });
    }

    /**
     * 在Ui线程调用请求错误回调方法
     *
     * @param callback 回调对象
     * @param code     请求错误码
     * @param response 返回消息
     */
    private void sendOnErrorMessage(final Callback callback, final int code, final Response response) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(code, response);
                callback.onEnd();
            }
        });
    }

}
