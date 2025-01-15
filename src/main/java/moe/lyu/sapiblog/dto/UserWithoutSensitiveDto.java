package moe.lyu.sapiblog.dto;

import lombok.Data;
import moe.lyu.sapiblog.entity.User;

@Data
public class UserWithoutSensitiveDto extends User {
    private Integer id;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private String role;
    private String jwt;
    private Long createTime;
    private Long updateTime;
    private Long lastLoginTime;
}
