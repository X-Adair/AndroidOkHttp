package com.adair.okhttp.builder;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.adair.okhttp.OkHttp;
import com.adair.okhttp.callback.DownloadCallback;
import com.adair.okhttp.callback.OkHttpDownloadCallback;
import com.adair.okhttp.interceptor.DownloadInterceptor;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * created at 2018/7/10 10:55
 *
 * @author XuShuai
 * @version v1.0
 */
public class DownloadBuilder extends BaseRequestBuilder<DownloadBuilder> {

    private OkHttpClient okHttpClient;

    //保存的文件夹地址
    private String fileDir;

    //保存的下载文件的文件名
    private String fileName;

    //是否删除已有相同文件名的文件，默认不删除,新建文件名加编号
    private boolean isDeleteOriginFile = false;

    public DownloadBuilder(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    /**
     * 保存下载文件的文件夹地址
     *
     * @param fileDir file directory
     */
    public DownloadBuilder fileDir(@NonNull String fileDir) {
        this.fileDir = fileDir;
        return this;
    }

    /**
     * 保存下载文件的文件名
     *
     * @param fileName file name
     */
    public DownloadBuilder fileName(@NonNull String fileName) {
        this.fileName = fileName;
        return this;
    }

    /**
     * 删除已有文件
     *
     * @param deleteOriginFile 是否删除已有相同文件名的文件
     */
    public DownloadBuilder deleteOriginFile(boolean deleteOriginFile) {
        isDeleteOriginFile = deleteOriginFile;
        return this;
    }

    /**
     * 异步下载
     *
     * @param callback 下载回调实现对象
     */
    public void enqueue(final DownloadCallback callback) {
        String filePath = null;
        try {
            filePath = getRealPath(url, fileDir, fileName, isDeleteOriginFile);
        } catch (final IOException e) {
            OkHttp.getUiHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onFail(e);
                    callback.onFinish();
                }
            });
        }
        OkHttpDownloadCallback okHttpCallback = new OkHttpDownloadCallback(filePath, callback);
        callback.onStart();
        Request request = buildRequest();
        Call call = okHttpClient.newBuilder()
                                .addNetworkInterceptor(new DownloadInterceptor(callback))
                                .build()
                                .newCall(request);
        OkHttp.addDownloadCall(call);
        call.enqueue(okHttpCallback);
    }

    /**
     * 同步下载
     *
     * @return 结果Response
     * @throws IOException IO异常
     */
    public Response execute() throws IOException {
        Request request = buildRequest();
        return okHttpClient.newCall(request).execute();
    }

    /**
     * 构建Request
     *
     * @return OkHttp Request
     */
    private Request buildRequest() {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalStateException("url can not be null !");
        }
        Request.Builder builder = new Request.Builder();
        buildHeaders(builder, headers);
        builder.url(url);
        if (tag != null) {
            builder.tag(tag);
        }
        return builder.build();
    }


    /**
     * 检测保存文件的路径是否正确
     *
     * @param url      下载地址
     * @param fileDir  保存文件的文件夹路径
     * @param fileName 保存文件的文件名
     */
    private String getRealPath(String url, String fileDir, String fileName, boolean isDeleteOriginFile) throws IOException {
        File file = new File(fileDir);
        if (!file.isDirectory()) {
            throw new IOException("fileDir is not a directory.");
        }
        Log.d("fileDir", file.getAbsolutePath());
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IOException("Failed to create fileDir!");
            }
        }

        if (fileName == null || fileName.length() == 0) {
            fileName = url.substring(url.lastIndexOf("/"));
        }

        File realFile = new File(fileDir, fileName);
        int i = 1;

        while (realFile.exists()) {
            if (isDeleteOriginFile) {
                if (!realFile.delete()) {
                    throw new IOException("Failed to delete origin file!");
                }
            } else {
                int dotIndex = fileName.lastIndexOf(".");
                String fileNameOther;
                if (dotIndex == -1) {
                    fileNameOther = fileName + "(" + i + ")";
                } else {
                    fileNameOther = fileName.substring(0, dotIndex) + "(" + i + ")" + fileName.substring(dotIndex);
                }
                realFile = new File(fileDir, fileNameOther);
                i++;
            }
        }
        return realFile.getAbsolutePath();
    }
}
