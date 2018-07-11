package com.adair.okhttp.callback;

import com.adair.okhttp.OkHttp;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 普通请求回调实现
 * <p>
 * created at 2018/7/2 13:58
 *
 * @author XuShuai
 * @version v1.0
 */
public abstract class GsonCallback<T> implements ICallback {

    private Gson gson;

    private Type type;

    public GsonCallback() {
        this.type = getSuperclassTypeParameter(getClass());
        gson = new Gson();
    }

    /**
     * 通过反射想要的返回类型
     *
     * @param subclass 目标类
     * @return type
     */
    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onSuccess(int code, Response response) {
        if (code == 200) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                try {
                    String responseBodyString = responseBody.string();
                    if (type == String.class) {
                        successRunOnUiThread((T) responseBodyString);
                    } else {
                        Object o = gson.fromJson(responseBodyString, type);
                        successRunOnUiThread((T) o);
                    }
                } catch (IOException e) {
                    errorRunOnUiThread(code, response);
                }
            } else {
                successRunOnUiThread(null);
            }

        } else {
            errorRunOnUiThread(code, response);
        }
        response.close();
    }

    @Override
    public void onFailure(IOException e) {
        failureRunOnUiThread(e);
    }

    /**
     * 服务器响应成功并且code不为200
     *
     * @param t 响应结果转为需要的对象
     */
    public abstract void onSuccess(T t);

    /**
     * 服务器响应成功但code不为200
     *
     * @param code     服务器响应code
     * @param response 服务器响应结果
     */
    public abstract void onError(int code, Response response);


    /**
     * 服务器未响应
     *
     * @param e 错误信息
     */
    public abstract void onFail(IOException e);


    private void failureRunOnUiThread(final IOException e) {
        OkHttp.getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                onFail(e);
                onFinish();
            }
        });
    }

    private void errorRunOnUiThread(final int code, final Response response) {
        OkHttp.getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                onError(code, response);
                onFinish();
            }
        });
    }

    private void successRunOnUiThread(final T t) {
        OkHttp.getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                onSuccess(t);
                onFinish();
            }
        });
    }
}
