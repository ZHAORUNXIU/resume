package com.x.resume.common.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    /**
     * 保存但不覆盖原有数据(字段有空或者""数据不保存)
     */
    T saveNotNull(T entity);

    /**
     * 分页查询
     */
    List<T> findByPage(Page<T> page);

    /**
     * 数量查询
     */
    long count(Specification<T> specification);

    /**
     * 批量插入
     */
    void batchInsert(List<T> list);

    /**
     * 批量更新
     */
    void batchUpdate(List<T> list);

    /**
     * 查询
     *
     * @param sql sql
     * @return List
     */
    List executeSelect(String sql);

}
