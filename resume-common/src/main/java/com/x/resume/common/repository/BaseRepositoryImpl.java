package com.x.resume.common.repository;

import com.google.common.collect.Maps;
import com.x.resume.common.annotation.CreatedAt;
import com.x.resume.common.annotation.UpdatedAt;
import com.x.resume.common.util.DateUtil;
import com.x.resume.common.util.Log;
import com.x.resume.common.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@NoRepositoryBean
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private static final Logger LOG = LoggerFactory.getLogger(BaseRepositoryImpl.class);


    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em;


    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.em = entityManager;
    }

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
        this(JpaEntityInformationSupport.getEntityInformation(domainClass, em), em);
    }

    @Override
    @Transactional
    public T saveNotNull(T entity) {
        //获取ID
        ID entityId = (ID) this.entityInformation.getId(entity);
        T managedEntity;
        T mergedEntity;
        if (entityId == null) {
            // 设置创建时间
            this.setTimestampField(entity, Boolean.TRUE);
            em.persist(entity);
            mergedEntity = entity;
        } else {
            Optional<T> optional = this.findById(entityId);
            if (!optional.isPresent()) {
                // 设置创建时间
                this.setTimestampField(entity, Boolean.TRUE);
                em.persist(entity);
                mergedEntity = entity;
            } else {
                managedEntity = optional.get();
                BeanUtils.copyProperties(entity, managedEntity, getNullProperties(entity));
                // 设置更新时间
                this.setTimestampField(managedEntity, Boolean.FALSE);
                em.merge(managedEntity);
                mergedEntity = managedEntity;
            }
        }
        return mergedEntity;
    }

    private static String[] getNullProperties(Object src) {
        //1.获取Bean
        BeanWrapper srcBean = new BeanWrapperImpl(src);
        //2.获取Bean的属性描述
        PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
        //3.获取Bean的空属性
        Set<String> properties = new HashSet<>();
        for (PropertyDescriptor propertyDescriptor : pds) {
            String propertyName = propertyDescriptor.getName();
            Object propertyValue = srcBean.getPropertyValue(propertyName);
            if (null == propertyValue) {
                srcBean.setPropertyValue(propertyName, null);
                properties.add(propertyName);
            }
        }
        return properties.toArray(new String[0]);
    }

    @Override
    public List<T> findByPage(Page<T> page) {
        List<T> list = this.findAll(page.getSpecification(), page.getPageable()).getContent();
        return Page.reverse(new ArrayList<>(list), page);
    }

    @Override
    public long count(Specification<T> specification) {
        return super.count(specification);
    }

    @Override
    @Transactional
    public void batchInsert(List<T> list) {
//        for (T entity : list) {
//            em.persist(entity);
//        }
//        em.flush();
//        em.clear();
        batchInsertSQL(list);
    }

    @Override
    @Transactional
    public void batchUpdate(List<T> list) {
        for (T entity : list) {
            em.merge(entity);
        }
        em.flush();
        em.clear();
    }

    /**
     * 批量保存
     *
     * @param list
     */
    private void batchInsertSQL(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        //开始的小括号
        String signFieldStart = "(";
        //结束的小括号
        String signFieldEnd = ")";
        // 空格
        String signSpace = " ";
        // 逗号
        String signComma = ",";
        // 分号
        String signSemicolon = ";";
        // 单引号
        String signQuotation = "'";

        StringBuilder sql = new StringBuilder("INSERT INTO");

        // 拼接表名
        String tableName = this.getTableName(list.get(0));
        if (Text.isEmpty(tableName)) {
            return;
        }
        sql.append(signSpace).append(tableName).append(signSpace);

        // 拼接字段
        Map<String, String> fields = this.getFields(list.get(0));
        if (CollectionUtils.isEmpty(fields)) {
            return;
        }
        sql.append(signFieldStart);
        fields.forEach((k, v) -> sql.append(k).append(signComma));
        sql.deleteCharAt(sql.length() - 1).append(signFieldEnd);

        // 拼接值
        sql.append(signSpace).append("VALUES").append(signSpace);
        for (T entity : list) {
            sql.append(signFieldStart);
            Map<String, String> values = this.getFields(entity);
            values.forEach((k, v) -> {
                if ("null".equals(v)) {
                    sql.append(v);
                } else {
                    sql.append(signQuotation).append(v).append(signQuotation);
                }
                sql.append(signComma);
            });
            sql.deleteCharAt(sql.length() - 1).append(signFieldEnd).append(signComma);
        }
        sql.deleteCharAt(sql.length() - 1).append(signSemicolon);

        em.createNativeQuery(sql.toString()).executeUpdate();
        em.flush();
        em.clear();
    }

    @Override
    public List executeSelect(String sql){
        List list = em.createNativeQuery(sql).getResultList();
        em.flush();
        em.clear();
        return list;
    }

    /**
     * 获取表名
     *
     * @param entity 实体类
     * @return
     */
    private String getTableName(T entity) {
        Annotation[] annotations = entity.getClass().getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Table) {
                Table table = entity.getClass().getDeclaredAnnotation(Table.class);
                return table.name();
            }
        }
        return Text.EMPTY;
    }

    /**
     * 设置时间戳
     *
     * @param entity
     * @param isCreated
     * @return
     */
    private void setTimestampField(T entity, Boolean isCreated) {
        Field[] fields = entity.getClass().getDeclaredFields();
        long now = Instant.now().toEpochMilli();
        try {
            for (Field field : fields) {
                if ("serialVersionUID".equals(field.getName())) {
                    continue;
                }

                // 更新
                if (field.isAnnotationPresent(UpdatedAt.class)) {
                    // private 也可以访问值
                    field.setAccessible(true);
                    field.set(entity, now);
                }

                // 创建
                if (isCreated && field.isAnnotationPresent(CreatedAt.class)) {
                    // private 也可以访问值
                    field.setAccessible(true);
                    field.set(entity, now);
                }
            }
        } catch (IllegalAccessException exception) {
            LOG.error(Log.format("批量保存失败-获取字段值失败"), exception);
        }
    }

    /**
     * 获取所有字段值
     *
     * @param entity 实体类
     * @return
     */
    private Map<String, String> getFields(T entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        Map<String, String> map = Maps.newTreeMap();

        try {
            for (Field field : fields) {
                if ("serialVersionUID".equals(field.getName())) {
                    continue;
                }

                if (field.isAnnotationPresent(Column.class)) {
                    String fieldName = field.getAnnotation(Column.class).name();
                    // private 也可以访问值
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    map.put(fieldName, obj2String(value));
                }
            }
        } catch (IllegalAccessException exception) {
            LOG.error(Log.format("批量保存失败-获取字段值失败"), exception);
            return Maps.newHashMap();
        }
        return map;
    }

    private String obj2String(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).toPlainString();
        }
        if (value instanceof Date) {
            return DateUtil.formatDate((Date) value, DateUtil.FORMAT.YYYY_MM_DD_HH_MM_SS);
        }
        return value.toString();
    }

}

