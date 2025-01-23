package moe.lyu.sapiblog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

@Data
@TableName("\"user\"")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String salt;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private String role;
    private String jwt;
    private String totp;
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;
    private Long lastLoginTime;

    public void setUsername(String username) {
        this.username = username.toLowerCase().trim();
    }

    public void setRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            role = "USER";
        }
        this.role = role;
    }

    public void setLastLoginTime() {
        this.lastLoginTime = new Date().toInstant().getEpochSecond();
    }

    public void setTotp(String totp) {
        if (totp == null) {
            totp = "";
        }
        this.totp = totp;
    }
}
