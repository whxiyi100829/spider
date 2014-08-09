package com.app.lgr.spider.util;

import com.google.common.collect.Queues;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * User: hzwangxx
 * Date: 14-8-9
 * Time: 10:01
 */
public class SpiderUtil {

    private SpiderUtil() {

    }

    /**
     * 创建目录，如果文件，则删除重新建目录
     * @param dir 待创建的目录
     * @return 成功返回true，失败false
     */
    public static boolean createDir(String dir) {
        boolean success;
        File file = new File(dir);
        try {
            if (file.exists()) {
                if (file.isFile()) {  //如果文件，则删除重新建目录
                    file.delete();
                    success = file.mkdirs();
                } else {
                    success = true;
                }
            } else {
                success = file.mkdirs();
            }
        } catch (Exception e) {
           throw new RuntimeException("error.", e);
        }
        return success;
    }

    /**
     * 下载文件，并存到本地
     * @param srcUrl network url
     * @param fileName  local filePath
     * @return  success return true, otherwise false
     */
    public static boolean downloadFile(String srcUrl, String fileName) {
        boolean success = false;
        InputStream in = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(srcUrl);
            in = url.openStream();
            fos = new FileOutputStream(fileName);
            int b;
            while ((b = in.read()) != -1) {
                fos.write(b);
            }
            success = true;
        } catch (MalformedURLException e) {
            throw new RuntimeException("new URL error.", e);
        } catch (IOException e) {
            throw new RuntimeException("io exception.", e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("close stream error. ", e);
            }

        }
        return success;

    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String[] args) throws InterruptedException {
        /*String resDir = "/Users/apple/tmp/xxxx/xxx/";
        String uuid = uuid();
        String url = "http://pnewsapp.tc.qq.com/newsapp_bt/0/2k43fhac7iw1406386892/640";
        System.out.println(SpiderUtil.createDir(resDir));
        System.out.println(uuid);
        System.out.println(downloadFile(url, resDir + uuid + ".jpg"));*/
        boolean tag = false;
        final BlockingQueue<String> queue = Queues.newArrayBlockingQueue(1000);

        int threadCount = 5;
        final ExecutorService downloadExecutor = Executors.newFixedThreadPool(DynamicConfig.getInt("download.thread.num", threadCount));
        for(int i=0; i<threadCount; i++) {
            downloadExecutor.submit(new DownloadTask(queue));
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<1000; i++) {
                    String temp = String.format("%4d", i);
                    System.out.println("offer " + temp);
                    try {
                        queue.put(temp);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        t.setDaemon(true);
        t.start();
        downloadExecutor.shutdown();
        /*
        downloadExecutor.shutdown();
        if (!downloadExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
                System.exit(0);
        }*/


    }


    private static class DownloadTask implements Runnable{
        private BlockingQueue<String> queue;

        private DownloadTask(final BlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            boolean flag = true;
            while (flag) {
                try {
                    String temp = queue.poll(5, TimeUnit.SECONDS);
                    if (temp == null) {
                        break;
                    }
                    System.out.println(Thread.currentThread() + " poll " + temp);
                } catch (InterruptedException e) {
                    System.out.println("interrupted.");
                    flag = false;
                }
            }
        }
    }
}
