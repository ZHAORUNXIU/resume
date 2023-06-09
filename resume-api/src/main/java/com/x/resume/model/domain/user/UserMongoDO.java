package com.x.resume.model.domain.user;

import com.x.resume.common.annotation.CreatedAt;
import com.x.resume.common.annotation.UpdatedAt;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户
 *
 * @author runxiu.zhao
 * @date 2023-06-07 18:00:00
 */
@Document(collection = "user")
@Entity
public class UserMongoDO implements Serializable {

    private static final long serialVersionUID = 8718060005385905866L;

    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * userId@
     */
    @Indexed
    private Long userId;

    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别0未知1男2女
     */
    private Integer gender;

    /**
     * 生日
     */
    private Integer birth;

    @CreatedAt
    private Long createdAt;

    /**
     * 修改时间
     */
    @UpdatedAt
    private Long updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
