package moe.lyu.sapiblog.dto;

import lombok.Data;

@Data
public class UserLoginDto {
    private String username;
    private String password;
    private String totp;

}
