package com.x.resume.gateway.req.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 用户登录请求参数
 *
 * @author runxiu.zhao
 * @date 2023-05-20 16:00:00
 */
@ApiModel(value = "用户登录请求参数")
public class LoginReq {

    @NotNull
    @Length(min = 9, max = 12)
    @ApiModelProperty(value = "电话号码", example = "18888888888")
    private String phone;

    @NotNull
    @Length(min = 6, max = 6)
    @ApiModelProperty(value = "登录验证码", example = "123456")
    private String code;


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
