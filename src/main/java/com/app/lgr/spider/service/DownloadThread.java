package com.app.lgr.spider.service;

import com.app.lgr.spider.model.DownloadFile;
import com.app.lgr.spider.util.SpiderUtil;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * User: hzwangxx
 * Date: 14-8-9
 * Time: 17:02
 */
public class DownloadThread implements Runnable {
    private final static Logger LOG = Logger.getLogger(DownloadThread.class);
    private BlockingQueue<DownloadFile> queue;

    public DownloadThread(BlockingQueue<DownloadFile> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        boolean flag = true;
        while (flag) {
            try {
                DownloadFile downloadFile = queue.poll(10, TimeUnit.SECONDS);
                if (downloadFile == null) {
                    LOG.debug("close download thread.....");
                    break;
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("download file " + downloadFile);
                }
                try {
                    SpiderUtil.downloadFile(downloadFile.getSrcUrl(), downloadFile.getFileName());
                } catch (Exception e) {
                    LOG.warn("download error.", e);
                }
            } catch (InterruptedException e) {
                flag = false;
            }
        }
    }
}
