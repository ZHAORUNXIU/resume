package com.x.resume.annotation;

import java.lang.annotation.*;

/**
 * @author runxiu.zhao
 * 更新字段
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
public @interface UpdatedAt {

}
