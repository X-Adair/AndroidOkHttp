package com.adair.okhttp.builder;

import com.adair.okhttp.OkHttp;

import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Request;

/**
 * 带参数构建请求
 * <p>
 * created at 2018/7/9 9:40
 *
 * @author XuShuai
 * @version v1.0
 */
public abstract class BaseRequestBuilderWithParam<T extends BaseRequestBuilderWithParam> extends BaseRequestBuilder<T> {

    protected Map<String, String> params;


    public BaseRequestBuilderWithParam() {
        params = new LinkedHashMap<>();
        if (OkHttp.getCommonParams() != null && !OkHttp.getCommonParams().isEmpty()) {
            params.putAll(OkHttp.getCommonParams());
        }
    }

    public T addParam(String key, String value) {
        params.put(key, value);
        return (T) this;
    }

    public T params(Map<String, String> params) {
        this.params = params;
        return (T) this;
    }

    /**
     * 将参数写入Request
     *
     * @param builder OkHttp Request builder
     * @param params  Request param map
     */
    protected abstract void buildParams(Request.Builder builder, Map<String, String> params);
}
