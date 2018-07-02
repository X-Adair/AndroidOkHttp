package com.adair.okhttp;

import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 回调接口
 * <p>
 * created at 2018/6/20 10:45
 *
 * @author XuShuai
 * @version v1.0
 */
public abstract class Callback<T> {
    /**
     * T泛型对应的类型
     */
    Type type;

    protected Callback() {
        this.type = getSuperclassTypeParameter(getClass());
    }

    /**
     * 通过反射获取响应的T泛型的返回类型
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

    /**
     * 请求之前的回调
     * <p>
     * created at 2018/6/20 10:47
     */
    public void onStart() {
    }

    /**
     * 请求之后的回调，无论是否请求成功
     * <p>
     * created at 2018/6/20 10:48
     */
    public void onEnd() {
    }

    /**
     * 请求成功回调
     * <p>
     * created at 2018/6/20 10:50
     *
     * @param t 回调结果
     */
    public abstract void onSuccess(T t);

    /**
     * 当请求成功，但是code不为200时，调用此接口
     * <p>
     * created at 2018/6/20 10:51
     *
     * @param code     http返回code
     * @param response 请求response
     */
    public abstract void onError(int code, Response response);

    /**
     * 请求失败
     * <p>
     * created at 2018/6/21 15:13
     */
    public void onFailure(Call call, IOException e) {
    }


}
