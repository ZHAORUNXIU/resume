package com.x.resume.gateway.resp.user;

import com.x.resume.model.vo.category.CategoryVO;

import java.util.List;

/**
 * 用户生日设置请求参数
 *
 * @author runxiu.zhao
 * @date 2023-02-22 14:00:00
 */
public class UserInfoResp {

    /**
     * 性别
     */
    Integer gender;

    /**
     * 生日
     */
    Integer birth;

    /**
     * 用户感兴趣分类
     */
    List<Long> userInterestedIds;

    /**
     * 所有1级分类
     */
    List<CategoryVO> categoryList;


    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getBirth() {
        return birth;
    }

    public void setBirth(Integer birth) {
        this.birth = birth;
    }

    public List<Long> getUserInterestedIds() {
        return userInterestedIds;
    }

    public void setUserInterestedIds(List<Long> userInterestedIds) {
        this.userInterestedIds = userInterestedIds;
    }

    public List<CategoryVO> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryVO> categoryList) {
        this.categoryList = categoryList;
    }

}
