package com.adair.okhttp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 下载管理器
 * <p>
 * created at 2018/6/28 20:06
 *
 * @author XuShuai
 * @version v1.0
 */
public class DownloadManager {

    private static final String TAG = "DownloadManager";

    private static final DownloadManager ourInstance = new DownloadManager();

    /**
     * okHttpClient
     */
    private static OkHttpClient sOkHttpClient;

    /**
     * 存放下载请求
     */
    private static HashMap<String, Call> sDownCallMap;


    private static Handler mUiHandler;

    public static DownloadManager getInstance() {
        return ourInstance;
    }

    private DownloadManager() {
        sOkHttpClient = new OkHttpClient.Builder().build();
        sDownCallMap = new HashMap<>();
        mUiHandler = new Handler(Looper.getMainLooper());
    }

    public void cancel(String url) {
        Call call = sDownCallMap.get(url);
        if (call != null) {
            call.cancel();
        }
        sDownCallMap.remove(url);
    }


    public void download(final DownloadInfo downloadInfo, final DownloadCallback callback) {
        if (sDownCallMap.containsKey(downloadInfo.getUrl())) {
            return;
        }

        Runnable downloadRunnable = new Runnable() {

            private boolean isCancel = false;
            private boolean isPause = false;


            @Override
            public void run() {
                Log.e(TAG, "run: 开始执行");
                //在主线程下载开始前调用此方法
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onStart();
                    }
                });

//                //先做一次请求，获取下载文件总长度
//                Request previewRequest = new Request.Builder().url(downloadInfo.getUrl()).build();
//                final Call previewCall = sOkHttpClient.newCall(previewRequest);
//                final Response previewResponse;
//                final int code;
//                try {
//                    previewResponse = previewCall.execute();
//                    code = previewResponse.code();
//                } catch (final IOException e) {
//                    e.printStackTrace();
//                    mUiHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            callback.onFailure(previewCall, e);
//                            callback.onEnd();
//                        }
//                    });
//
//                    return;
//                }
//                if (previewResponse.isSuccessful()) {
//                    ResponseBody responseBody = previewResponse.body();
//                    if (responseBody != null) {
//                        long total = responseBody.contentLength();
//                        downloadInfo.setTotal(total);
//                        responseBody.close();
//                    } else {
//                        mUiHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                callback.onError(code, previewResponse);
//                                callback.onEnd();
//                            }
//                        });
//                        return;
//                    }
//                } else {
//                    mUiHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            callback.onError(code, previewResponse);
//                            callback.onEnd();
//                        }
//                    });
//                    return;
//                }




                long downloadLength = downloadInfo.getProgress();
                long contentLength = downloadInfo.getTotal();
                //下载
                Request downloadRequest = new Request.Builder()
                        .addHeader("Range", "bytes=" + downloadLength+"-")
                        .url(downloadInfo.getUrl())
                        .build();
                final Call call = sOkHttpClient.newCall(downloadRequest);
                sDownCallMap.put(downloadInfo.getUrl(), call);
                final Response downloadResponse;
                try {
                    downloadResponse = call.execute();
                } catch (final IOException e) {
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(call, e);
                            callback.onEnd();
                        }
                    });
                    e.printStackTrace();
                    return;
                }

                if (downloadResponse.isSuccessful()) {
                    final ResponseBody responseBody = downloadResponse.body();
                    if (responseBody != null) {
                        contentLength = responseBody.contentLength();
                        downloadInfo.setTotal(contentLength);
                        getRealFileName(downloadInfo);
                        Log.d(TAG, "run: contentLength" + contentLength);
                        File file = new File(downloadInfo.getSavePath(), downloadInfo.getFileName());
                        InputStream is = responseBody.byteStream();
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(file, true);
                            byte[] buffer = new byte[2048];
                            int len;
                            while ((len = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                                downloadLength += len;
                                downloadInfo.setProgress(downloadLength);
                                mUiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onProgress(downloadInfo.getProgress(), downloadInfo.getTotal());
                                    }
                                });
                            }
                            fos.flush();
                            sDownCallMap.remove(downloadInfo.getUrl());
                            mUiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onSuccess(downloadInfo.getSavePath() + "/" + downloadInfo.getFileName());
                                    callback.onEnd();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            mUiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError(downloadResponse.code(), downloadResponse);
                                    callback.onEnd();
                                }
                            });
                        } finally {
                            if (is != null) {
                                try {
                                    is.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                Log.d(TAG, "run: 任务完成");
            }
        };
        ThreadPoolManager.getInstance().executor(downloadRunnable);
    }

    /**
     * 获取文件信息
     *
     * @param downloadInfo 下载文件信息
     */
    private void getRealFileName(DownloadInfo downloadInfo) {
        //获取已下载文件信息
        String fileName = downloadInfo.getFileName();
        long downloadLength = 0;
        long contentLength = downloadInfo.getTotal();
        File file = new File(downloadInfo.getSavePath(), downloadInfo.getFileName());
        if (file.exists()) {
            //找到文件代表已下载过该文件
            downloadLength = file.length();
        }
        int i = 1;
        //如果已经下载过该文件,则生成一个新文件
        while (downloadLength >= contentLength) {
            int dotIndex = fileName.lastIndexOf(".");
            String fileNameOther;
            if (dotIndex == -1) {
                fileNameOther = fileName + "i";
            } else {
                fileNameOther = fileName.substring(0, dotIndex) + "(" + i + ")" + fileName.substring(dotIndex);
            }

            File newFile = new File(downloadInfo.getSavePath(), fileNameOther);
            file = newFile;
            downloadLength = newFile.length();
            i++;
        }
        downloadInfo.setProgress(downloadLength);
        downloadInfo.setFileName(file.getName());
    }
}
