package com.app.lgr.spider.service;

import com.app.lgr.spider.model.NewsCategory;

import java.util.List;

/**
 * User: hzwangxx
 * Date: 14-7-27
 * Time: 10:54
 */
public interface NewsCategoryService {

    /**
     * 查询所有的新闻栏目列表
     * @return 新闻栏目列表
     */
    List<NewsCategory> queryAllCategories() throws ServiceException;

    /**
     * 根据id查询新闻栏目
     * @param id 新闻栏目ID
     * @return  新闻栏目详情
     */
    NewsCategory queryCategoryById(Integer id) throws ServiceException;

}
