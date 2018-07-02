package com.adair.okhttp;

import java.io.File;

/**
 * created at 2018/6/29 14:17
 *
 * @author XuShuai
 * @version v1.0
 */
public class DownloadRequest {

    private DownloadInfo downloadInfo;

    private DownloadRequest(DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
    }


    public void download(DownloadCallback callback) {
        DownloadManager.getInstance().download(downloadInfo, callback);
    }


    public static class Builder {
        private String url;
        private String savePath;
        private String fileName;
        private long contentLength;
        private long totalLength;

        public Builder() {
        }

        public Builder(String url) {
            this.url = url;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder savePath(String savePath) throws Exception {
            File file = new File(savePath);
            if (!file.isDirectory()) {
                throw new Exception("该路径不是一个文件夹路径");
            }
            this.savePath = savePath;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public DownloadRequest build() {
            DownloadInfo downloadInfo = new DownloadInfo();
            downloadInfo.setUrl(url);
            downloadInfo.setSavePath(savePath);
            if (fileName == null) {
                fileName = url.substring(url.lastIndexOf("/"));
            }
            downloadInfo.setFileName(fileName);
            downloadInfo.setTotal(0);
            downloadInfo.setProgress(0);
            return new DownloadRequest(downloadInfo);
        }
    }
}
