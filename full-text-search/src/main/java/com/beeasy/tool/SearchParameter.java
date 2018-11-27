package com.beeasy.tool;

import java.util.Date;

public class SearchParameter {
    long   uid;
    String keyword;
    Date   createMin;
    Date createMax;
    Date modifyMin;
    Date modifyMax;
    int page;
    int size;
    String sortField = "modifyTime";
    String sortType = "desc";

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Date getCreateMin() {
        return createMin;
    }

    public void setCreateMin(Date createMin) {
        this.createMin = createMin;
    }

    public Date getCreateMax() {
        return createMax;
    }

    public void setCreateMax(Date createMax) {
        this.createMax = createMax;
    }

    public Date getModifyMin() {
        return modifyMin;
    }

    public void setModifyMin(Date modifyMin) {
        this.modifyMin = modifyMin;
    }

    public Date getModifyMax() {
        return modifyMax;
    }

    public void setModifyMax(Date modifyMax) {
        this.modifyMax = modifyMax;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }
}
