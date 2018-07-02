package com.adair.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adair.okhttp.Callback;
import com.adair.okhttp.DownloadCallback;
import com.adair.okhttp.DownloadManager;
import com.adair.okhttp.DownloadRequest;
import com.adair.okhttp.OkHttpManager;
import com.adair.okhttp.OkHttpRequest;
import com.adair.okhttp.test.GetRequest;
import com.adair.okhttp.test.OkHttpClientManager;
import com.adair.okhttp.test.Request;
import com.android.base.ActivityBase;

import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;

public class MainActivity extends ActivityBase implements View.OnClickListener {

    private static final String TAG = "MainActivity";


    private TextView resultTextView;

    private ProgressBar mProgressBar1;

    private String url = "http://wdl1.cache.wps.cn/wps/download/W.P.S.5554.50.345.exe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTextView = findViewById(R.id.result);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        mProgressBar1 = findViewById(R.id.progressBar1);
        Request request = new Request(OkHttpClientManager.getDefaultOkHttpClient(false));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button1:
                doGetRequest();
                break;
            case R.id.button2:
                doPostRequest();
                break;
            case R.id.button3:
                download();
                break;
            case R.id.button4:
                DownloadManager.getInstance().cancel(url);
                break;
        }
    }


    private void doGetRequest() {
        new OkHttpRequest.Builder()
                .url("https://www.baidu.com")
                .get()
                .build()
                .enqueue(new Callback<String>() {

                    @Override
                    public void onStart() {
                        resultTextView.setText("");
                        resultTextView.append("开始请求\n");
                    }


                    @Override
                    public void onSuccess(String s) {
                        resultTextView.append(s + "\n");
                    }

                    @Override
                    public void onError(int code, Response response) {
                        resultTextView.append("onError: " + code + "  " + response.message() + "\n");
                        Log.d(TAG, "onError: " + code + "  " + response.message());
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        resultTextView.append(e.getMessage() + "\n");
                    }

                    @Override
                    public void onEnd() {
                        resultTextView.append("结束请求");
                    }

                });
    }

    private void doPostRequest() {
        new OkHttpRequest.Builder()
                .url("http://192.168.1.18:8080/addUser")
                .addParam("cellphone", "15184365180")
                .addParam("nickname", "一叶知秋")
                .addParam("realname", "胥帅")
                .addParam("idCard", "511333333333333333")
                .addParam("birthday", "1993-02-10")
                .addParam("signature", "一任阶前点滴到天明")
                .addParam("address", "中国四川省成都市")
                .post()
                .build()
                .enqueue(new Callback<String>() {

                    @Override
                    public void onStart() {
                        resultTextView.setText("");
                        resultTextView.append("开始请求\n");
                    }


                    @Override
                    public void onSuccess(String s) {
                        resultTextView.append(s + "\n");
                    }

                    @Override
                    public void onError(int code, Response response) {
                        resultTextView.append("onError: " + code + "  " + response.message() + "\n");
                        Log.d(TAG, "onError: " + code + "  " + response.message());
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        resultTextView.append(e.getMessage() + "\n");
                    }

                    @Override
                    public void onEnd() {
                        resultTextView.append("结束请求");
                    }
                });
    }


    private void download() {
        try {
            new DownloadRequest.Builder(url)
                    .savePath(getExternalFilesDir("download").getAbsolutePath())
                    .build()
                    .download(new DownloadCallback() {

                        @Override
                        public void onStart() {
                            resultTextView.setText("");
                            resultTextView.append("开始请求\n");
                        }

                        @Override
                        public void onSuccess(String t) {
                            resultTextView.append("下载完成\n" + t);
                        }

                        @Override
                        public void onProgress(long downloadLength, long totalLength) {
                            Log.d(TAG, "onProgress: " + (downloadLength / totalLength));
                            int i = (int) (downloadLength * 100f / totalLength);
                            Log.d(TAG, "onProgress: " + i);
                            mProgressBar1.setProgress(i);
                        }

                        @Override
                        public void onError(int code, Response response) {
                            resultTextView.append("onError: " + code + "  " + response.message() + "\n");
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            super.onFailure(call, e);
                            resultTextView.append(e.getMessage() + "\n");
                        }

                        @Override
                        public void onEnd() {
                            super.onEnd();
                            resultTextView.append("结束请求");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
