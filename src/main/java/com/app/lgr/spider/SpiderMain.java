package com.app.lgr.spider;

import com.app.lgr.spider.model.NewsItem;
import com.app.lgr.spider.ex.SpiderException;
import com.app.lgr.spider.service.DownloadThread;
import com.app.lgr.spider.service.SpiderWorker;
import com.google.common.collect.Queues;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Queue;

/**
 * User: hzwangxx
 * Date: 14-7-27
 * Time: 11:28
 * Spider主程序
 */
public class SpiderMain {

    private static final Logger LOG = Logger.getLogger(SpiderMain.class);
    private static Queue<String> hasExtractLinks;
    private static final String CACHE_LINKS_FILE = "links.json";
    private static final int HAS_EXTRACT_LINKS_QUEUE_CAPACITY = 200;
    static SpiderWorker spiderWorker;

    private static void init() {
        LOG.info(String.format(".......... init to spider job ........."));
        //shutdown保存已抓取过的link
        Runtime.getRuntime().addShutdownHook(new CleanWorkThread());
        //已爬取过的新闻链接队列
        hasExtractLinks = Queues.newLinkedBlockingQueue(HAS_EXTRACT_LINKS_QUEUE_CAPACITY);
        //load已经爬取过得链接
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(CACHE_LINKS_FILE)));
            String link;
            while((link = br.readLine()) != null) {
                hasExtractLinks.offer(link);
            }
            LOG.info("init has extract size is " + hasExtractLinks.size());
        } catch (FileNotFoundException e) {
            LOG.warn("file not found, maybe this is first start. ", e);
        } catch (IOException e) {
            LOG.error("init error when load crawled links. ", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOG.error("close buffered reader error.", e);
                }
            }
        }
        //当hasExtractLinks队列满则pop
        Thread popQueueAfterFullThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = true;
                while (flag) {
                    try {
                        if (hasExtractLinks.size() == HAS_EXTRACT_LINKS_QUEUE_CAPACITY) {
                            String link = hasExtractLinks.poll();
                            LOG.info(String.format("pop link(%s) which has extracted.", link));
                        }
                    } catch (Exception e) {
                        LOG.error("pop link error.", e);
                        flag = false;
                    }

                }
            }
        }, "popQueueThread");
        popQueueAfterFullThread.setDaemon(true);
        popQueueAfterFullThread.start();

    }

    static class CleanWorkThread extends Thread{
        @Override
        public void run() {
            LOG.info(".........starting to clean work........");
            if (hasExtractLinks.size() > 0) {
                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new FileWriter(new File(CACHE_LINKS_FILE)));
                    for (String link : hasExtractLinks) {
                        bw.write(link);
                        bw.newLine();
                    }
                } catch (IOException e) {
                    LOG.error("save has extract link error.", e);
                } finally {
                    try {
                        if (bw != null) {
                            bw.flush();
                            bw.close();
                        }
                    } catch (IOException e) {
                        LOG.error("close file error.", e);
                    }
                }
            }
            if (spiderWorker.getDownloadFileList().size() > 0) {
                new Thread(new DownloadThread(spiderWorker.getDownloadFileList())).start();
            }
            LOG.info("..........shutdown spider........");
        }
    }

    private static void startSpider() {
        try {
            spiderWorker = SpiderWorker.getSpiderWorker();
            List<NewsItem> newsItems = spiderWorker.extractNewsItemsByJsoup(hasExtractLinks);
            int crawlSize = spiderWorker.saveNewsItems(newsItems);
            LOG.info(String.format("crawl %d news items", crawlSize));
        } catch (SpiderException e) {
            LOG.error(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        init();
        startSpider();
        spiderWorker.cleanWork();
    }

}
