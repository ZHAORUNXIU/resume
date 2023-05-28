package com.x.resume.gateway.req.user;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 用户性别设置请求参数
 *
 * @author runxiu.zhao
 * @date 2023-05-20 16:00:00
 */
public class UserGenderReq {

    /**
     * 性别 1男 2女
     */
    @NotNull
    @Min(1)
    @Max(2)
    private Integer gender;

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

}
