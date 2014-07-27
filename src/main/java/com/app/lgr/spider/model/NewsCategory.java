package com.app.lgr.spider.model;

import com.google.common.base.Objects;

/**
 * User: hzwangxx
 * Date: 14-7-26
 * Time: 21:12
 * 新闻类别
 */
public class NewsCategory {
    private Integer id;
    private String name;
    private String url;
    private String desc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("url", url)
                .add("desc", desc)
                .toString();
    }
}
