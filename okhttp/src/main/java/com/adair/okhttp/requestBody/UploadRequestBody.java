package com.adair.okhttp.requestBody;

import com.adair.okhttp.OkHttp;
import com.adair.okhttp.callback.UploadCallback;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * created at 2018/7/12 15:50
 *
 * @author XuShuai
 * @version v1.0
 */
public class UploadRequestBody extends RequestBody {

    private RequestBody requestBody;
    private UploadCallback callback;

    private BufferedSink bufferedSink;

    public UploadRequestBody(RequestBody requestBody, UploadCallback callback) {
        this.requestBody = requestBody;
        this.callback = callback;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }


    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(sink(sink));
        }
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            private long currentBytes = 0;
            private long totalBytes = 0;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (totalBytes == 0) {
                    totalBytes = contentLength();
                }
                currentBytes += byteCount;
                OkHttp.getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onProgress(currentBytes, totalBytes);
                    }
                });
            }
        };
    }

}
