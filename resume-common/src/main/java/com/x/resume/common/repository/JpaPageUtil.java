package com.x.resume.common.repository;

import com.x.resume.common.constant.Constant;
import com.x.resume.common.util.Text;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Jpa分页
 *
 * @author runxiu.zhao
 * @date 2023-03-06 14:00:00
 */
public class JpaPageUtil {

    /**
     * 构建查询条件
     */
    public static <T> Specification<T> buildSpecification(Paging paging) {
        return buildSpecification(paging.getConditions());
    }

    /**
     * 构建查询条件
     */
    public static <T> Specification<T> buildSpecification(Map<String, Object> conditions) {
        String suffixStart = "Start";
        String suffixEnd = "End";
        String notEqual = "NotEqual";
        String isNull = "IsNull";
        String like = "Like";

        // 构建查询条件对象
        return (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();

            // 设置搜索条件
            if (!CollectionUtils.isEmpty(conditions)) {
                conditions.keySet().forEach(key -> {
                    Object value = conditions.get(key);
                    if (Objects.isNull(value) && !key.endsWith(isNull)) {
                        return;
                    }
                    if (value instanceof List) {
                        if (key.endsWith(like)) {
                            Path<String> objectPath = root.get(key.replace(like, Text.EMPTY));
                            List<Predicate> predicateList = new ArrayList<>();
                            Predicate[] p = new Predicate[((List<?>) value).size()];
                            for (Object item : (List<?>) value) {
                                // 模糊查询
                                predicateList.add(cb.like(objectPath, Constant.PERCENT_SIGN + item.toString() + Constant.PERCENT_SIGN));
                            }
                            predicateList.toArray(p);
                            list.add(cb.or(p));
                        } else {
                            CriteriaBuilder.In<Integer> in = cb.in(root.get(key));
                            for (Object item : (List<?>) value) {
                                in.value(Integer.valueOf(item.toString()));
                            }
                            list.add(in);
                        }
                    } else if (key.endsWith(suffixStart)) {
                        list.add(cb.greaterThanOrEqualTo(root.get(key.replace(suffixStart, Text.EMPTY)).as(Long.class), (long) value));
                    } else if (key.endsWith(suffixEnd)) {
                        list.add(cb.lessThan(root.get(key.replace(suffixEnd, Text.EMPTY)).as(Long.class), (long) value));
                    } else if (key.endsWith(notEqual)) {
                        list.add(cb.notEqual(root.get(key.replace(notEqual, Text.EMPTY)), value));
                    } else if (key.endsWith(isNull)) {
                        list.add(cb.isNull(root.get(key.replace(isNull, Text.EMPTY))));
                    } else if (key.endsWith(like)) {
                        // 模糊查询
                        Path<String> objectPath = root.get(key.replace(like, Text.EMPTY));
                        list.add(cb.like(objectPath, Constant.PERCENT_SIGN + value.toString() + Constant.PERCENT_SIGN));
                    } else {
                        list.add(cb.equal(root.get(key), value));
                    }
                });
            }
            return cb.and(list.toArray(new Predicate[0]));
        };
    }

    public static <T> Page<T> buildPage(Paging paging) {
        // 分页主键字段
        String sortField = Constant.ID;

        // 设置查询条件
        Specification<T> whereSpecification = buildSpecification(paging.getConditions());

        // 设置分页条件
        Specification<T> pageSpecification = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (paging.getFrom() != 0) {
                if (paging.getDirect() == 1) {
                    list.add(cb.lessThan(root.get(sortField).as(Long.class), paging.getFrom()));
                }
                if (paging.getDirect() == -1) {
                    list.add(cb.greaterThan(root.get(sortField).as(Long.class), paging.getFrom()));
                }
            }
            return cb.and(list.toArray(new Predicate[0]));
        };
        Specification<T> specification = whereSpecification.and(pageSpecification);

        // 构建分页对象
        Sort sort = Sort.by(paging.getDirect() == 1 ? Sort.Direction.DESC : Sort.Direction.ASC, sortField);
        Pageable pageable = PageRequest.of(0, paging.getLimit(), sort);
        return Page.of(paging, specification, pageable);
    }
}

