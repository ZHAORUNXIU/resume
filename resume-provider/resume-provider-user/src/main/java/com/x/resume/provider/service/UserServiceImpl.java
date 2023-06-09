package com.x.resume.provider.service;

import com.x.resume.api.user.UserService;
import com.x.resume.common.client.RedisClient;
import com.x.resume.common.constant.Constant;
import com.x.resume.common.constant.RedisConstant;
import com.x.resume.common.constant.UserCode;
import com.x.resume.common.model.Environment;
import com.x.resume.common.model.Result;
import com.x.resume.common.util.AES256Util;
import com.x.resume.common.util.Log;
import com.x.resume.common.util.Text;
import com.x.resume.common.util.TokenEncryptUtil;
import com.x.resume.model.domain.user.UserDO;
import com.x.resume.model.domain.user.UserMongoDO;
import com.x.resume.provider.repository.UserMongoRepository;
import com.x.resume.provider.repository.UserRepository;
import com.x.resume.types.user.UserStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.GeneralSecurityException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.x.resume.common.util.Log.format;
import static com.x.resume.common.util.Log.kv;

/**
 * 用户Service
 *
 * @author runxiu.zhao
 * @date 2023-05-20 16:00:00
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String LOG_PREFIX = "用户管理:";

//    @Resource
//    private Environment environment;

    @Resource
    private UserRepository userRepository;

    @Resource
    private UserMongoRepository userMongoRepository;

    @Resource
    private RedisClient redisClient;

    /**
     * 用户登录有效时间 分钟
     */
    @Value("${user.login.tokenExpire:525600}")
    private int tokenExpire;

    /**
     * 密钥
     */
    @Value("${app.authKey}")
    private String authKey;


    @Override
    public Result<UserDO> get(Long id) {
        return userRepository.getById(id).map(Result::success).orElseGet(() -> Result.failure(UserCode.USER_NOT_FOUND));
    }

    @Override
    public Result<UserDO> getDecode(Long id) {
        return userRepository.getById(id).map(item -> Result.success(this.decode(item))).orElseGet(() -> Result.failure(UserCode.USER_NOT_FOUND));
    }

    @Override
    public Result<UserDO> getByUserId(Long userId) {
        return userRepository.getByUserId(userId).map(Result::success).orElseGet(() -> Result.failure(UserCode.USER_NOT_FOUND));
    }

    @Override
    public Result<UserDO> getByUserIdDecode(Long userId) {
        return null;
    }

    @Override
    public Result<UserDO> get(String phone) {
        return userRepository.getByPhone(encode(phone.replaceAll(Constant.REGEX_ZERO_START, Text.EMPTY))).map(Result::success).orElseGet(() -> Result.failure(UserCode.USER_NOT_FOUND));
    }

    @Override
    public Result<String> login(String phone, String code) {
        // 验证短信验证码
        Result<Void> checkCodeResult = checkCode(phone, code);
        if (!checkCodeResult.isSuccess()) {
            return Result.failure(checkCodeResult);
        }

        UserDO user;

        Result<UserDO> result = this.get(phone);
        // 手机号没有注册，直接注册再登录
        if (result.isSuccess()) {
            user = result.getData();
        } else if (UserCode.USER_NOT_FOUND == result.getCode()) {
            Result<UserDO> registerResult = this.register(phone);
            if (!registerResult.isSuccess()) {
                return Result.failure(registerResult);
            }
            user = registerResult.getData();
        } else {
            return Result.failure(result);
        }

        if (!user.getState().equals(UserStateEnum.NORMAL.getCode())) {
            LOG.warn(format("用户状态异常", kv("user_id", user.getId()), kv("state", user.getState())));
            return Result.failure(UserCode.USER_STATUS_LOCAL);
        }

        String token = generateToken(user.getUserId());
        if (token == null) {
            LOG.warn(format("生成token失败", kv("user_id", user.getUserId())));
            return Result.failure(UserCode.LOGIN_FAILED);
        }

        int expireTime = (int) TimeUnit.MINUTES.toSeconds(tokenExpire);
        String tokenKey = RedisConstant.USER_LOGIN_TOKEN_KEY + token;
        redisClient.setex(tokenKey, user.getUserId(), expireTime);
        return Result.success(token);
    }

    @Override
    public Result<Void> setGender(Long userId, Integer gender) {
        Result<UserDO> result = this.getByUserId(userId);
        if (!result.isSuccess()) {
            return Result.failure(result);
        }
        UserDO userDO = result.getData();
        userDO.setGender(gender);
        userRepository.saveNotNull(userDO);
        return Result.success(null);
    }

    @Override
    public Result<Void> setBirth(Long userId, Integer birth) {
        Result<UserDO> result = this.getByUserId(userId);
        if (!result.isSuccess()) {
            return Result.failure(result);
        }
        UserDO userDO = result.getData();
        userDO.setBirth(birth);
        userRepository.saveNotNull(userDO);
        return Result.success(null);
    }

    @Override
    public Result<String> addToMongo(UserMongoDO userMongoDO) {
        userMongoDO.setUserId(generateUserId());
        userMongoDO = userMongoRepository.save(userMongoDO);
        return Result.success(userMongoDO.getId());
    }

    /**
     * 注册
     *
     * @param phone 手机号
     * @return Result<UserDO>
     */
    private Result<UserDO> register(String phone) {
        UserDO userDO = new UserDO();
        userDO.setUserId(this.generateUserId());
        userDO.setPhone(encode(phone.replaceAll(Constant.REGEX_ZERO_START, Text.EMPTY)));
        userDO.setState(UserStateEnum.NORMAL.getCode());
        userDO = userRepository.saveNotNull(userDO);

        return Result.success(userDO);
    }

    private Long generateUserId() {
        Random random = new Random();
        int min = 100000000;
        int max = 999999999;
        int userId = random.nextInt(max - min + 1) + min;
        return Long.valueOf(userId);
    }

    /**
     * 解密用户所有加密字段
     *
     * @param userDO 实体
     * @return UserAuthDO
     */
    private UserDO decode(UserDO userDO) {
        UserDO newUserDO = new UserDO();
        BeanUtils.copyProperties(userDO, newUserDO);
        if (UserStateEnum.NORMAL.getCode().equals(newUserDO.getState())) {
            newUserDO.setEmail(decode(newUserDO.getEmail()));
            newUserDO.setPhone(decode(newUserDO.getPhone()));
        }
        return newUserDO;
    }

    /**
     * 检查登录验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return Boolean
     */
    private Result<Void> checkCode(String phone, String code) {
        // 测试环境忽略密码判断
//        if ((environment.isDev() || environment.isLocal()) && Constant.DEFAULT_CODE.equals(code)) {
//            return Result.success(null);
//        }
        String key = RedisConstant.USER_LOGIN_CODE_KEY + phone;
        String redisCode = redisClient.get(key);
        if (Text.isEmpty(redisCode) || !redisCode.equals(code)) {
            LOG.warn(Log.format("用户登录验证码错误", Log.kv("code", code), Log.kv("redisCode", redisCode)));
            return Result.failure(UserCode.LOGIN_CODE_INCORRECT);
        }
        return Result.success(null);
    }

    /**
     * 生成token
     *
     * @param userId 用户id
     * @return String
     */
    private String generateToken(Long userId) {
        try {
            String source = userId.toString() + Constant.UNDERLINE + (System.currentTimeMillis() / 1000) + Constant.UNDERLINE + new Random().nextInt(100000000);
            return TokenEncryptUtil.encryptToken(source);
        } catch (GeneralSecurityException e) {
            LOG.error(Log.format("生成Token失败"), e);
        }
        return null;
    }

    /**
     * 加密
     *
     * @param content 明文
     * @return 密文
     */
    private String encode(String content) {
        return AES256Util.encode(authKey, content);
    }

    /**
     * 解密
     *
     * @param cipher 密文
     * @return 明文
     */
    private String decode(String cipher) {
        return AES256Util.decode(authKey, cipher);
    }

}
