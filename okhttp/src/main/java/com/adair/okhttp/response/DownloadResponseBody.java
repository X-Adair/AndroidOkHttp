package com.adair.okhttp.response;

import com.adair.okhttp.OkHttp;
import com.adair.okhttp.callback.DownloadCallback;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * created at 2018/7/11 9:59
 *
 * @author XuShuai
 * @version v1.0
 */
public class DownloadResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private DownloadCallback callback;
    private BufferedSource bufferedSource;

    public DownloadResponseBody(ResponseBody responseBody, DownloadCallback callback) {
        this.responseBody = responseBody;
        this.callback = callback;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }


    private Source source(Source source) {
        return new ForwardingSource(source) {

            long currentBytes;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                currentBytes += bytesRead != -1 ? bytesRead : 0;
                OkHttp.getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onProgress(currentBytes, contentLength());
                    }
                });
                return bytesRead;
            }
        };
    }
}
