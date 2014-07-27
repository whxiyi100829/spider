package com.app.lgr.spider.util;

import com.app.lgr.spider.model.*;
import com.app.lgr.spider.service.NewsCategoryService;
import com.app.lgr.spider.service.NewsItemService;
import com.app.lgr.spider.service.ServiceException;
import com.app.lgr.spider.service.impl.NewsCategoryServiceImpl;
import com.app.lgr.spider.service.impl.NewsItemServiceImpl;
import com.google.common.collect.Lists;
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

/**
 * User: hzwangxx
 * Date: 14-7-26
 * Time: 21:07
 */
public class SpiderUtil {

    private static NewsCategoryService newsCategoryService  = new NewsCategoryServiceImpl();
    private static NewsItemService newsItemService = new NewsItemServiceImpl();

    private static final Logger LOG = Logger.getLogger(SpiderUtil.class);

    private  static  final String[] DATE_FORMATS = {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"};

    /**
     * 保存News Items
     * @param newsItems list of NewsItem
     * @return count of saved
     */
    public static int saveNewsItems(List<NewsItem> newsItems) {
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
     * @throws SpiderException SpiderException
     */
    public static List<NewsItem> extractNewsItemsByJsoup(Queue<String> hasExtractLinks) throws SpiderException {
        List<NewsItem> newsItems = Lists.newArrayList();
        //1. 需要抓取的类别信息
        List<NewsCategory> newsCategories;
        try {
            newsCategories = newsCategoryService.queryAllCategories();
        } catch (ServiceException e) {
            LOG.error("query categories error.", e);
            throw new SpiderException("extract news item error. ", e);
        }

        for (NewsCategory newsCategory : newsCategories) {
            //2. 取新闻Item的链接
            Document linkDoc;
            try {
                linkDoc = Jsoup.connect(newsCategory.getUrl()).get();
            } catch (IOException e) {
                LOG.error(String.format("crawl url(%s) error.", newsCategory.getUrl()), e);
                continue;
            }
            Elements newsLinks = linkDoc.select(".pubbox ul li a");
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
                item.setContent(content.html());

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
