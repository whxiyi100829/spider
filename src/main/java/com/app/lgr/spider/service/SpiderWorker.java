package com.app.lgr.spider.service;

import com.app.lgr.spider.ex.ServiceException;
import com.app.lgr.spider.model.*;
import com.app.lgr.spider.service.impl.NewsCategoryServiceImpl;
import com.app.lgr.spider.service.impl.NewsItemServiceImpl;
import com.app.lgr.spider.ex.SpiderException;
import com.app.lgr.spider.util.DynamicConfig;
import com.app.lgr.spider.util.SpiderUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: hzwangxx
 * Date: 14-7-26
 * Time: 21:07
 */
public class SpiderWorker {

    private static SpiderWorker spiderWorker;
    private String resDir;
    private String resServer;
    private NewsCategoryService newsCategoryService  = new NewsCategoryServiceImpl();
    private NewsItemService newsItemService = new NewsItemServiceImpl();
    private static BlockingQueue<DownloadFile> downloadFileList = Queues.newArrayBlockingQueue(500);

    private static final Logger LOG = Logger.getLogger(SpiderWorker.class);

    private static  final String[] DATE_FORMATS = {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"};
    ExecutorService downloadExecutor;
    private SpiderWorker() {
        //开启异步下载图片线程
        int threadNum = DynamicConfig.getInt("download.thread.num", 10);
        downloadExecutor = Executors.newFixedThreadPool(threadNum);
        for (int i=0; i<threadNum; i++) {
            downloadExecutor.execute(new DownloadThread(downloadFileList));
        }

        resServer = DynamicConfig.getStr("resource.server", "");
        if (!resServer.endsWith("/")) {
            resServer = resServer + "/";
        }
        resDir = DynamicConfig.getStr("resource.directory", "temp");
        if (!resDir.endsWith("/")) {
            resDir = resDir + "/";
        }

    }

    /**
     * 清理现场
     */
    public void cleanWork() throws InterruptedException {
        System.out.println("shutdown executor.............");
        downloadExecutor.shutdown();
        /*if (!downloadExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
            System.exit(-1);
        }*/
    }

    public static SpiderWorker getSpiderWorker() {
        if (spiderWorker == null) {
            spiderWorker = new SpiderWorker();
        }
        return spiderWorker;
    }

    public BlockingQueue<DownloadFile> getDownloadFileList() {
        return downloadFileList;
    }

    /**
     * 保存News Items
     * @param newsItems list of NewsItem
     * @return count of saved
     */
    public int saveNewsItems(List<NewsItem> newsItems) {
        if (newsItems != null && newsItems.size() > 0) {
            try {
                return newsItemService.batchInsertNewsItem(newsItems);
            } catch (ServiceException e) {
                LOG.error("batch insert error.", e);
            }
        }
        return 0;
    }

    /**
     * 抓取页面信息
     * @param hasExtractLinks 已经抓取过的
     * @return list of NewsItem
     * @throws com.app.lgr.spider.ex.SpiderException SpiderException
     */
    public List<NewsItem> extractNewsItemsByJsoup(Queue<String> hasExtractLinks) throws SpiderException {

        String datePath = DateFormatUtils.format(new Date(), "yyyyMMdd/");
        //创建当前日期目录
        try {

            boolean flag = SpiderUtil.createDir(resDir + datePath);
            if (!flag) {
                LOG.info("create resource directory error.");
                System.exit(-1);
            }
        } catch (Exception e) {
            LOG.error("create resource directory error.", e);
            System.exit(-1);
        }

        List<NewsItem> newsItems = Lists.newArrayList();
        //1. 需要抓取的类别信息
        List<NewsCategory> newsCategories;
        try {
            newsCategories = newsCategoryService.queryAllCategories();
        } catch (ServiceException e) {
            LOG.error("query categories error.", e);
            throw new SpiderException("extract news item error. ", e);
        }
        int i=0;
        for (NewsCategory newsCategory : newsCategories) {
            //2. 取新闻Item的链接
            Document linkDoc;
            try {
                linkDoc = Jsoup.connect(newsCategory.getUrl()).get();
            } catch (IOException e) {
                LOG.error(String.format("crawl url(%s) error.", newsCategory.getUrl()), e);
                continue;
            }
            Elements focusLink = linkDoc.select(".focus a");
            Elements newsLinks = linkDoc.select(".pubbox ul li a");
            newsLinks.addAll(focusLink);

            i++;
            if (i == 4) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (Element element : newsLinks) {

                String newsUrl = element.attr("href");
                if (hasExtractLinks.contains(newsUrl)) {
                    break;
                }

                //3. 抓取页面信息
                Document newsDoc;
                try {
                    newsDoc = Jsoup.connect(newsUrl).get();
                } catch (IOException e) {
                    LOG.error(String.format("crawl url(%s) error.", newsUrl), e);
                    continue;
                }
                Elements newsContent = newsDoc.select(".content");
                NewsItem item = new NewsItem();
                // 3.1 title
                Elements globalTitle = newsContent.select("#global_title");
                item.setTitle(globalTitle.text());

                // 3.2 date & source
                Elements globalMeta = newsContent.select("#global_meta");
                String dateStr = globalMeta.select(".content-info-time").text();
                String source = globalMeta.select(".content-info-source").text();
                try {
                    item.setCreateTime(DateUtils.parseDate(dateStr, DATE_FORMATS));
                } catch (ParseException e) {
                    LOG.warn("parse date error for " + dateStr);
                    item.setCreateTime(new Date());
                }
                item.setSource(source);

                // 3.3 content
                Elements content = newsContent.select("#content");
                // 3.4 清掉video
                Elements videos = content.select(".Tencent-main-player");
                for (Element video : videos) {
                    video.remove();
                }
                String contentHtml = content.html();

                // 3.5 提取图片并下载
                Elements images = content.select("img");
                for (Element imgElemt : images) {
                    String imgUrl = imgElemt.absUrl("src");
                    String fileName =  datePath + SpiderUtil.uuid() + ".jpg";
                    String absolutePath = resDir  + fileName;
                    String relativePath = resServer + fileName;
                    DownloadFile downloadFile = new DownloadFile(imgUrl, absolutePath);
                    try {
                        downloadFileList.put(downloadFile);
                    } catch (InterruptedException e) {
                        LOG.warn("put download file interrupted.", e);
                    }
                    contentHtml = contentHtml.replace(imgUrl, relativePath);
                }
                item.setContent(contentHtml);

                item.setHits(0l);
                item.setCategoryId(newsCategory.getId());

                newsItems.add(item);

                //4. 缓存页面链接
                hasExtractLinks.offer(newsUrl);
            }
        }
        return newsItems;
    }



}

