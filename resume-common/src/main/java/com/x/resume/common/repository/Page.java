package com.x.resume.common.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Page<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int direct;

    private int limit = 10;

    private long from;

    private Specification<T> specification;
    private Pageable pageable;


    public Page() {
    }

    public static <E> Page<E> of(Paging paging, Specification<E> specification, Pageable pageable) {
        return new Page<E>(paging.getFrom(), paging.getDirect(), paging.getLimit(), specification, pageable);
    }

    private Page(Long from, Integer direct, Integer limit, Specification<T> specification, Pageable pageable) {
        this.from = from;
        this.direct = direct;
        this.limit = limit;
        this.specification = specification;
        this.pageable = pageable;
    }

    /**
     * 上一页翻转
     *
     * @param list 待翻转列表
     * @param page 翻页对象
     * @param prev 上一页代码
     * @param <T>  泛型实体类
     * @return 翻转后的列表
     */
    public static <T> List<T> reverse(List<T> list, Page<T> page, int prev) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        if (page.getFrom() != 0 && page.getDirect() == prev) {
            Collections.reverse(list);
        }
        return list;
    }

    /**
     * 上一页翻转
     *
     * @param list 待翻转列表
     * @param page 翻页对象
     * @param <T>  泛型实体类
     * @return 翻转后的列表
     */
    public static <T> List<T> reverse(List<T> list, Page<T> page) {
        return reverse(list, page, -1);
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public long getFrom() {
        return this.from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public int getDirect() {
        return this.direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }

    public Specification<T> getSpecification() {
        return specification;
    }

    public void setSpecification(Specification<T> specification) {
        this.specification = specification;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }
}
