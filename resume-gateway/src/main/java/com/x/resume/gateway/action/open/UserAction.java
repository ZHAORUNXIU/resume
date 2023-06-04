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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.hibernate.validator.constraints.Length;
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
import java.util.Random;
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
@Api
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
    @ApiOperation(value = "用户登录", notes = "未注册用户自动注册")
    @ApiResponses({@ApiResponse(code = 404, response = String.class, message = "接口不存在"),
            @ApiResponse(code = 500, response = String.class, message = "程序错误")})
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
    @ApiOperation(value = "发送登录验证码", notes = "登录前的短信验证")
    public Result<Void> sendLoginCode(@RequestParam(required = true) @NotNull @Length(min = 9, max = 12) String phone) {

        String key = RedisConstant.USER_LOGIN_CODE_KEY + phone;

        String redisCode = redisClient.get(key);
        if (!Text.isEmpty(redisCode)) {
            return Result.failure(UserCode.LOGIN_CODE_NOT_EXPIRED);
        }

        String code = generateRandomNumber();
        Result result = aliyunClient.sendMessage(phone, code);
        if (!result.isSuccess()) {
            return Result.failure(result);
        }
        int expireTime = (int) TimeUnit.MINUTES.toSeconds(codeExpire);
        redisClient.setex(key, code, expireTime);

        return Result.success(null);
    }

    private static String generateRandomNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000; // 生成一个 100,000 到 999,999 之间的随机数
        return String.format("%06d", randomNumber);
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
