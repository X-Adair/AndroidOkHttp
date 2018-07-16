package com.adair.okhttp.callback;

import com.adair.okhttp.OkHttp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * OkHttp3 下载文件回调实现
 * <p>
 * created at 2018/7/10 16:22
 *
 * @author XuShuai
 * @version v1.0
 */
public class OkHttpDownloadCallback implements Callback {

    private DownloadCallback callback;
    //保存文件地址
    private String filePath;

    public OkHttpDownloadCallback(String filePath, DownloadCallback callback) {
        this.callback = callback;
        this.filePath = filePath;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        failureRunOnUiThread(callback, e);
    }

    @Override
    public void onResponse(Call call, Response response) {
        try {
            ResponseBody body = response.body();
            if (body != null) {
                byte[] buffer = new byte[2048];
                File file = new File(filePath);
                InputStream is = null;
                FileOutputStream fos = null;
                try {
                    is = body.byteStream();
                    fos = new FileOutputStream(file);
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    successRunOnUiThread(callback, filePath);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                }
            }
        } catch (IOException e) {
            failureRunOnUiThread(callback, e);
        }
        OkHttp.removeDownloadCall(call);
    }

    public void failureRunOnUiThread(final DownloadCallback callback, final IOException e) {
        OkHttp.getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.onFail(e);
                callback.onFinish();
            }
        });
    }


    public void successRunOnUiThread(final DownloadCallback callback, final String t) {
        OkHttp.getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(t);
                callback.onFinish();
            }
        });
    }

    public void progressRunOnUiThread(final DownloadCallback callback, final long currentBytes, final long totalBytes) {
        OkHttp.getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.onProgress(currentBytes, totalBytes);
            }
        });
    }

}
