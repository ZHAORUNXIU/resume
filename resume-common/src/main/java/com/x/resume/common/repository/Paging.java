package com.x.resume.common.repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Paging implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 方向
     */
    private int direct;

    /**
     * 查询条数
     */
    private int limit = 10;

    /**
     * 起点
     */
    private long from;

    /**
     * 条件
     */
    private Map<String, Object> conditions = new HashMap<>();

    public void addCondition(String key, Object value) {
        conditions.put(key, value);
    }

    public void addConditions(Map<String, Object> conditions) {
        this.conditions.putAll(conditions);
    }

    public Map<String, Object> getConditions() {
        return conditions;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public int getDirect() {
        return direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }
}
