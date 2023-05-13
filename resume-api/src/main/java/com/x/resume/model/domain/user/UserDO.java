package com.x.resume.model.domain.user;

import com.x.resume.annotation.CreatedAt;
import com.x.resume.annotation.UpdatedAt;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户
 *
 * @author runxiu.zhao
 * @date 2023-02-21 10:00:00
 */
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "t_user")
public class UserDO implements Serializable {

    private static final long serialVersionUID = -750985702302517041L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "f_id")
    private Long id;

    /**
     * uid
     */
    @Column(name = "f_uid")
    private Long uid;

    /**
     * 用户名
     */
    @Column(name = "f_user_name")
    private String userName;

    /**
     * 昵称
     */
    @Column(name = "f_nick_name")
    private String nickName;

    /**
     * 真实姓名
     */
    @Column(name = "f_real_name")
    private String realName;

    /**
     * 手机号
     */
    @Column(name = "f_phone")
    private String phone;

    /**
     * 邮箱
     */
    @Column(name = "f_email")
    private String email;

    /**
     * 性别0未知1男2女
     */
    @Column(name = "f_gender")
    private Integer gender;

    /**
     * 生日
     */
    @Column(name = "f_birth")
    private Integer birth;

    /**
     * 状态1正常0禁用
     */
    @Column(name = "f_state")
    private Integer state;

    /**
     * 积分
     */
    @Column(name = "f_point")
    private Long point;

    /**
     * 地址
     */
    @Column(name = "f_address")
    private String address;

    /**
     * 头像
     */
    @Column(name = "f_photo")
    private String photo;

    /**
     * 创建时间
     */
    @CreatedAt
    @Column(name = "f_created_at")
    private Long createdAt;

    /**
     * 修改时间
     */
    @UpdatedAt
    @Column(name = "f_updated_at")
    private Long updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getPoint() {
        return point;
    }

    public void setPoint(Long point) {
        this.point = point;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
