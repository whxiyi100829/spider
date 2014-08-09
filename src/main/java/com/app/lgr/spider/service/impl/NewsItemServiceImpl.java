package com.app.lgr.spider.service.impl;

import com.app.lgr.spider.model.NewsItem;
import com.app.lgr.spider.service.NewsItemService;
import com.app.lgr.spider.ex.ServiceException;
import com.app.lgr.spider.util.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * User: hzwangxx
 * Date: 14-7-27
 * Time: 11:01
 */
public class NewsItemServiceImpl implements NewsItemService {
    private static final String INSERT_NEWS_ITEM_SQL = "INSERT INTO `news_item` (`title`, `content`, `source`, `create_time`, `hits`, `category_id`) " +
            "VALUES (?,?,?,?,?,?)";
    @Override
    public int batchInsertNewsItem(List<NewsItem> newsItems) throws ServiceException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int[] rs = {};

        try {
            conn = DBUtils.getConn();
            pstmt = conn.prepareStatement(INSERT_NEWS_ITEM_SQL);
            for (NewsItem item : newsItems) {
                if (item.isInvalid()) {
                    continue;
                }
                pstmt.setString(1, item.getTitle());
                pstmt.setString(2, item.getContent());
                pstmt.setString(3, item.getSource());
                pstmt.setTimestamp(4, new Timestamp(item.getCreateTime().getTime()));
                pstmt.setLong(5, item.getHits());
                pstmt.setInt(6, item.getCategoryId());
                pstmt.addBatch();
            }
            rs = pstmt.executeBatch();
        } catch (SQLException e) {
            throw new ServiceException("sql exception.", e);
        } catch (ClassNotFoundException e) {
            throw new ServiceException("class not found exception.", e);
        } finally {
            try {
                DBUtils.close(conn, pstmt, null);
            } catch (SQLException e) {
                throw new ServiceException("close error.", e);
            }
        }

        return rs.length;
    }

    /*public static void main(String[] args) throws ServiceException {
        NewsItemService newsItemService = new NewsItemServiceImpl();
        List<NewsItem> newsItems = Lists.newArrayList();
        NewsItem item = new NewsItem();
        item.setTitle("测试中文");
        item.setCategoryId(2);
        item.setCreateTime(new Date());
        newsItems.add(item);
        int rs = newsItemService.batchInsertNewsItem(newsItems);
        System.out.println(rs);
    }*/
}
