package com.app.lgr.spider.service;

import com.app.lgr.spider.ex.ServiceException;
import com.app.lgr.spider.model.NewsItem;

import java.util.List;

/**
 * User: hzwangxx
 * Date: 14-7-27
 * Time: 10:58
 */
public interface NewsItemService {

    /**
     * 批量插入新闻信息
     * @return 成功插入的数据条数
     */
    int batchInsertNewsItem(List<NewsItem> newsItems) throws ServiceException;
}
