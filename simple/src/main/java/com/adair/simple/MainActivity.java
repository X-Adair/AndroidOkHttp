package com.adair.simple;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adair.okhttp.OkHttp;
import com.adair.okhttp.callback.DownloadCallback;
import com.adair.okhttp.callback.GsonCallback;
import com.android.base.ActivityBase;

import java.io.IOException;

import okhttp3.Response;

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
                cancelDownload();
                break;
        }
    }


    private void doGetRequest() {
        OkHttp.get()
              .url("https://www.baidu.com")
              .tag(this)
              .enqueue(new GsonCallback<String>() {

                  @Override
                  public void onStart() {
                      super.onStart();
                      resultTextView.setText("");
                      resultTextView.append("开始请求\n");
                  }

                  @Override
                  public void onSuccess(String s) {
                      resultTextView.append("请求成功:\n");
                      resultTextView.append(s + "\n");
                  }

                  @Override
                  public void onError(int code, Response response) {
                      resultTextView.append("请求错误:\n");
                      resultTextView.append("code:" + code + "\n");
                      resultTextView.append("response:" + response.message() + "\n");
                  }

                  @Override
                  public void onFail(IOException e) {
                      resultTextView.append("请求失败:\n");
                      resultTextView.append(e.getMessage() + "\n");
                  }

                  @Override
                  public void onFinish() {
                      super.onFinish();
                      resultTextView.append("请求结束");
                  }
              });
    }

    private void doPostRequest() {
        OkHttp.post()
              .url("http://192.168.1.18:8080/addUser")
              .tag(this)
              .addParam("cellphone", "15184365180")
              .addParam("nickname", "落叶知秋")
              .addParam("realname", "胥帅")
              .addParam("idCard", "511325199302104689")
              .addParam("birthday", "1993-02-10")
              .addParam("signature", "一任阶前点滴到天明")
              .addParam("address", "成都孝沸科技有限公司")
              .enqueue(new GsonCallback<String>() {

                  @Override
                  public void onStart() {
                      super.onStart();
                      resultTextView.setText("");
                      resultTextView.append("开始请求\n");
                  }

                  @Override
                  public void onSuccess(String s) {
                      resultTextView.append("请求成功:\n");
                      resultTextView.append(s + "\n");
                  }

                  @Override
                  public void onError(int code, Response response) {
                      resultTextView.append("请求错误:\n");
                      resultTextView.append("code:" + code + "\n");
                      resultTextView.append("response:" + response.message() + "\n");

                  }

                  @Override
                  public void onFail(IOException e) {
                      resultTextView.append("请求失败:\n");
                      resultTextView.append(e.getMessage() + "\n");
                  }

                  @Override
                  public void onFinish() {
                      super.onFinish();
                      resultTextView.append("请求结束");
                  }
              });
    }


    private void download() {
        OkHttp.download()
              .url(url)
              .fileDir(getExternalFilesDir("download").getAbsolutePath())
              .deleteOriginFile(false)
              .fileName("W.P.S.5554.50.345.exe")
              .tag(this)
              .enqueue(new DownloadCallback() {

                  @Override
                  public void onStart() {
                      super.onStart();
                      resultTextView.setText("");
                      resultTextView.append("开始下载\n");
                  }

                  @Override
                  public void onSuccess(String path) {
                      resultTextView.append("下载成功\n");
                      resultTextView.append("文件地址:" + path + "\n");
                  }

                  @Override
                  public void onProgress(long currentBytes, long totalBytes) {
                      int progress = (int) (currentBytes * 100.00f / totalBytes);
                      mProgressBar1.setProgress(progress);
                  }

                  @Override
                  public void onFail(IOException e) {
                      resultTextView.append("下载失败\n");
                      resultTextView.append(e.getMessage());
                  }

                  @Override
                  public void onFinish() {
                      super.onFinish();
                      resultTextView.append("请求结束");
                  }
              });
    }


    private void cancelDownload(){
        OkHttp.cancel(this);
    }
}
