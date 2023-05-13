package com.x.resume.gateway.action.open;

import com.x.resume.annotation.Acl;
import com.x.resume.api.user.UserService;
import com.x.resume.common.web.ReqContext;
import com.x.resume.gateway.req.user.*;
import com.x.resume.gateway.resp.user.LoginResp;
import com.x.resume.gateway.traffic.TrafficControl;
import com.x.resume.gateway.traffic.TrafficControlType;
import com.x.resume.common.model.Result;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户
 *
 * @author runxiu.zhao
 * @date 2023-02-20 16:00:00
 */
@RestController
@RequestMapping(value = "/open/v1/user")
@Validated
@Component
public class UserAction {

    @Resource
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @TrafficControl(type = TrafficControlType.IP, threshold = 1000)
    public Result<LoginResp> login(@Validated @RequestBody LoginReq req) {
        Result<String> loginResult = userService.login(req.getPhone(), req.getCode());
        if (!loginResult.isSuccess()) {
            return Result.failure(loginResult);
        }
        LoginResp resp = new LoginResp();
        resp.setToken(loginResult.getData());
        return Result.success(resp);
    }

    /**
     * 设置用户性别
     */
    @Acl
    @PostMapping("/gender")
    @TrafficControl(type = TrafficControlType.USER, threshold = 20)
    public Result<Void> gender(@Validated @RequestBody UserGenderReq req) {
        Long userId = ReqContext.get().getUserId();
        userService.setGender(userId, req.getGender());
        return Result.success(null);
    }

    /**
     * 设置用户生日
     */
    @Acl
    @PostMapping("/birth")
    @TrafficControl(type = TrafficControlType.IP, threshold = 1000)
    public Result<Void> birth(@Validated @RequestBody UserBirthReq req) {
        Long userId = ReqContext.get().getUserId();
        userService.setBirth(userId, req.getBirth());
        return Result.success(null);
    }

}
