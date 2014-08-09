package com.app.lgr.spider.model;

/**
 * User: hzwangxx
 * Date: 14-8-9
 * Time: 12:45
 * dowload file model
 */
public class DownloadFile {

    /**网络地址*/
    private String srcUrl;

    /**存储本地文件名*/
    private String fileName;

    public String getSrcUrl() {
        return srcUrl;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return "DownloadFile{" +
                "srcUrl='" + srcUrl + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }

    public DownloadFile(String srcUrl, String fileName) {
        this.srcUrl = srcUrl;
        this.fileName = fileName;
    }
}
