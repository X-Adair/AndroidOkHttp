package com.adair.okhttp;

/**
 * 文件下载信息
 * <p>
 * created at 2018/6/28 20:26
 *
 * @author XuShuai
 * @version v1.0
 */
public class DownloadInfo {

    /**
     * 下载地址
     */
    private String url;

    /**
     * 保存文件名
     */
    private String fileName;

    /**
     * 保存文件地址
     */
    private String savePath;

    /**
     * 文件总长度
     */
    private long total;

    /**
     * 下载进度
     */
    private long progress;

    /**
     * tag，方便取消
     */
    private String tag;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", total=" + total +
                ", progress=" + progress +
                ", tag='" + tag + '\'' +
                '}';
    }
}
