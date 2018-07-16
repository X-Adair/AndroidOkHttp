package com.adair.okhttp.callback;

/**
 * created at 2018/7/12 16:09
 *
 * @author XuShuai
 * @version v1.0
 */
public abstract class UploadCallback<T> extends GsonCallback<T> {

    public abstract void onProgress(long currentBytes, long totalBytes);
}
