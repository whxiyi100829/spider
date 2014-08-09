package com.app.lgr.spider.service.impl;

import com.app.lgr.spider.model.NewsCategory;
import com.app.lgr.spider.service.NewsCategoryService;
import com.app.lgr.spider.ex.ServiceException;
import com.app.lgr.spider.util.DBUtils;
import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * User: hzwangxx
 * Date: 14-7-27
 * Time: 10:58
 */
public class NewsCategoryServiceImpl implements NewsCategoryService {
    private static final String QUERY_ALL_CATEGORIES = "SELECT `id`, `name`, `url`, `desc` FROM `news_category`";
    private static final String QUERY_CATEGORY_BY_ID = "SELECT `id`, `name`, `url`, `desc` FROM `news_category` where `id` = ?";

    @Override
    public List<NewsCategory> queryAllCategories() throws ServiceException {
        List<NewsCategory> newsCategories = Lists.newArrayList();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConn();
            pstmt = conn.prepareStatement(QUERY_ALL_CATEGORIES);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                NewsCategory newsCategory = new NewsCategory();
                newsCategory.setId(rs.getInt("id"));
                newsCategory.setName(rs.getString("name"));
                newsCategory.setUrl(rs.getString("url"));
                newsCategory.setDesc(rs.getString("desc"));
                newsCategories.add(newsCategory);
            }
        } catch (SQLException e) {
            throw new ServiceException("sql exception", e);
        } catch (ClassNotFoundException e) {
            throw new ServiceException("class not found exception", e);
        } finally {
            try {
                DBUtils.close(conn, pstmt, rs);
            } catch (SQLException e) {
                throw new ServiceException("sql exception", e);
            }
        }
        return newsCategories;
    }

    @Override
    public NewsCategory queryCategoryById(Integer id) throws ServiceException {
        NewsCategory newsCategory = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConn();
            pstmt = conn.prepareStatement(QUERY_CATEGORY_BY_ID);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newsCategory = new NewsCategory();
                newsCategory.setId(rs.getInt("id"));
                newsCategory.setName(rs.getString("name"));
                newsCategory.setUrl(rs.getString("url"));
                newsCategory.setDesc(rs.getString("desc"));
            }
        } catch (SQLException e) {
            throw new ServiceException("sql exception", e);
        } catch (ClassNotFoundException e) {
            throw new ServiceException("class not found exception", e);
        } finally {
            try {
                DBUtils.close(conn, pstmt, rs);
            } catch (SQLException e) {
                throw new ServiceException("sql exception", e);
            }
        }
        return newsCategory;
    }

    /*public static void main(String[] args) throws Exception {
        NewsCategoryService newsCategoryService = new NewsCategoryServiceImpl();
        List<NewsCategory> list = newsCategoryService.queryAllCategories();
        System.out.println(list);
        System.out.println(newsCategoryService.queryCategoryById(1));
    }*/
}
