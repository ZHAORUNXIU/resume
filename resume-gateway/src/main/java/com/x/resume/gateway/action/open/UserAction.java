package com.x.resume.gateway.action.open;

import com.x.resume.common.annotation.Acl;
import com.x.resume.api.user.UserService;
import com.x.resume.common.client.RedisClient;
import com.x.resume.common.constant.RedisConstant;
import com.x.resume.common.constant.UserCode;
import com.x.resume.common.manager.aliyun.AliyunClient;
import com.x.resume.common.util.Log;
import com.x.resume.common.util.Text;
import com.x.resume.common.web.ReqContext;
import com.x.resume.gateway.req.user.*;
import com.x.resume.gateway.resp.user.LoginResp;
import com.x.resume.gateway.traffic.TrafficControl;
import com.x.resume.gateway.traffic.TrafficControlType;
import com.x.resume.common.model.Result;
import com.x.resume.model.domain.user.UserMongoDO;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户
 *
 * @author runxiu.zhao
 * @date 2023-05-20 16:00:00
 */
@RestController
@RequestMapping(value = "/open/v1/user")
@Validated
@Component
public class UserAction {

    @Resource
    private UserService userService;

    @Resource
    private RedisClient redisClient;

    @Resource
    private AliyunClient aliyunClient;

    /**
     * 登录验证码有效时间 分钟
     */
    @Value("${user.login.codeExpire:5}")
    private int codeExpire;

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
     * 发送登录验证码
     */
    @GetMapping("/send")
    @TrafficControl(type = TrafficControlType.IP, threshold = 1000)
    public Result<Void> sendLoginCode(@RequestParam(required = true) @NotNull @Length(min = 9, max = 12) String phone) {

        String key = RedisConstant.USER_LOGIN_CODE_KEY + phone;

        String redisCode = redisClient.get(key);
        if (Text.isEmpty(redisCode)) {
            return Result.failure(UserCode.LOGIN_CODE_NOT_EXPIRED);
        }

        String code = UUID.randomUUID().toString().substring(0, 5);
        Result result = aliyunClient.sendMessage(phone, code);
        if (!result.isSuccess()) {
            return Result.failure(result);
        }
        int expireTime = (int) TimeUnit.MINUTES.toSeconds(codeExpire);
        redisClient.setex(key, code, expireTime);

        return Result.success(null);
    }

    /**
     * mongo test: Add to MongoDB
     */
    @PostMapping("/mongo/add")
    @TrafficControl(type = TrafficControlType.IP, threshold = 1000)
    public Result<String> addToMongo(@Validated @RequestBody AddToMongoReq req) {
        UserMongoDO userMongoDO = new UserMongoDO();
        BeanUtils.copyProperties(req, userMongoDO);
        Result<String> result = userService.addToMongo(userMongoDO);
        if (!result.isSuccess()) {
            return Result.failure(result);
        }
        return Result.success(result.getData());
    }

//    /**
//     * 设置用户性别
//     */
//    @Acl
//    @PostMapping("/gender")
//    @TrafficControl(type = TrafficControlType.USER, threshold = 20)
//    public Result<Void> gender(@Validated @RequestBody UserGenderReq req) {
//        Long userId = ReqContext.get().getUserId();
//        userService.setGender(userId, req.getGender());
//        return Result.success(null);
//    }

}
