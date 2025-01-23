package moe.lyu.sapiblog.dto;

import lombok.Data;

@Data
public class JwtDto {
    private Integer userId;
    private String username;
    private String role;
    private Long exp;
}
