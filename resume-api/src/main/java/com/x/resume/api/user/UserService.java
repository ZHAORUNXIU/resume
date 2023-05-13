package com.x.resume.api.user;

import com.x.resume.common.model.Result;
import com.x.resume.model.domain.user.UserDO;

import java.util.List;

/**
 * 用户Service
 *
 * @author runxiu.zhao
 * @date 2023-02-20 16:00:00
 */
public interface UserService {

    /**
     * 查询用户
     *
     * @param id 用户id
     * @return 用户
     */
    Result<UserDO> get(Long id);

    /**
     * 查询用户
     *
     * @param id 用户id
     * @return 用户
     */
    Result<UserDO> getDecode(Long id);

    /**
     * 查询用户
     *
     * @param uid 用户id
     * @return 用户
     */
    Result<UserDO> getByUid(Long uid);

    /**
     * 查询用户
     *
     * @param uid 用户id
     * @return 用户
     */
    Result<UserDO> getByUidDecode(Long uid);

    /**
     * 查询用户
     *
     * @param phone 用户手机号
     * @return 用户
     */
    Result<UserDO> get(String phone);

    /**
     * 登录
     *
     * @param phone 用户手机号
     * @param code  验证码
     * @return token
     */
    Result<String> login(String phone, String code);

    /**
     * 设置用户性别
     *
     * @param userId 用户id
     * @param gender 性别 1男 2女
     * @return Result
     */
    Result<Void> setGender(Long userId, Integer gender);

    /**
     * 设置用户生日
     *
     * @param userId 用户id
     * @param birth  生日 19900101
     * @return Result
     */
    Result<Void> setBirth(Long userId, Integer birth);

}
