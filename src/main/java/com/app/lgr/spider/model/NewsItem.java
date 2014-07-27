package com.app.lgr.spider.model;

import com.google.common.base.Objects;

import java.util.Date;

/**
 * User: hzwangxx
 * Date: 14-7-26
 * Time: 21:08
 * 新闻详细内容
 */
public class NewsItem {
    private Long id;
    private String title;
    private String content;
    private String source;
    private Date createTime;
    private Long hits = 0l;
    private Integer categoryId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getHits() {
        return hits;
    }

    public void setHits(Long hits) {
        this.hits = hits;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("title", title)
                .add("content", content)
                .add("source", source)
                .add("createTime", createTime)
                .add("hits", hits)
                .add("categoryId", categoryId)
                .toString();
    }
}
