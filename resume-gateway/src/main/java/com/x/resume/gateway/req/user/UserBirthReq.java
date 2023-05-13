package com.x.resume.gateway.req.user;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 用户生日设置请求参数
 *
 * @author runxiu.zhao
 * @date 2023-02-22 14:00:00
 */
public class UserBirthReq {

    /**
     * 生日
     */
    @NotNull
    @Min(19000101)
    private Integer birth;

    public Integer getBirth() {
        return birth;
    }

    public void setBirth(Integer birth) {
        this.birth = birth;
    }
}
