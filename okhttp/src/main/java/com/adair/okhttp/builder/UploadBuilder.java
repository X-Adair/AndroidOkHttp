package com.adair.okhttp.builder;

import android.text.TextUtils;

import com.adair.okhttp.callback.ICallback;
import com.adair.okhttp.callback.OkHttpCallBack;
import com.adair.okhttp.callback.UploadCallback;
import com.adair.okhttp.requestBody.UploadRequestBody;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * created at 2018/7/12 14:51
 *
 * @author XuShuai
 * @version v1.0
 */
public class UploadBuilder extends BaseRequestBuilderWithParam<UploadBuilder> {

    //需要上传的文件列表
    private List<String> fileList;
    private OkHttpClient okHttpClient;

    public UploadBuilder(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public UploadBuilder addFile(String filePath) {
        if (fileList == null) {
            fileList = new ArrayList<>();
        }
        fileList.add(filePath);
        return this;
    }


    /**
     * 异步执行上传请求方法
     *
     * @param callback 回调方法
     */
    public void enqueue(UploadCallback callback) {
        Request request = buildRequest();
        request = request.newBuilder().post(new UploadRequestBody(request.body(), callback)).build();
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


    /**
     * 生成请求Request
     *
     * @return OkHttp3 Request
     */
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


    @Override
    protected void buildParams(Request.Builder builder, Map<String, String> params) {
        MultipartBody.Builder multiparBodyBuilder = new MultipartBody.Builder();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                multiparBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        if (fileList != null && fileList.size() > 0) {
            for (String path : fileList) {
                File file = new File(path);
                if (!file.exists()) {
                    throw new IllegalArgumentException("file " + path + "  is not exist");
                } else {
                    multiparBodyBuilder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(guessMimeType(path)), file));
                }
            }
        }
        builder.post(multiparBodyBuilder.build());
    }


    /**
     * @param path 文件路径
     * @return 文件mime type
     */
    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

}
