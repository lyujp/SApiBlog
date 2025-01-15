package moe.lyu.sapiblog.mapper;

import moe.lyu.sapiblog.dto.UserWithoutSensitiveDto;
import moe.lyu.sapiblog.entity.User;

public class UserToUserWithoutSensitiveDtoMapper {
    public static UserWithoutSensitiveDto userToUserWithoutSensitiveDto(User user) {
        UserWithoutSensitiveDto userWithoutSensitiveDto = new UserWithoutSensitiveDto();
        userWithoutSensitiveDto.setId(user.getId());
        userWithoutSensitiveDto.setUsername(user.getUsername());
        userWithoutSensitiveDto.setNickname(user.getNickname());
        userWithoutSensitiveDto.setAvatar(user.getAvatar());
        userWithoutSensitiveDto.setEmail(user.getEmail());
        userWithoutSensitiveDto.setPhone(user.getPhone());
        userWithoutSensitiveDto.setRole(user.getRole());
        userWithoutSensitiveDto.setCreateTime(user.getCreateTime());
        userWithoutSensitiveDto.setUpdateTime(user.getUpdateTime());
        userWithoutSensitiveDto.setLastLoginTime(user.getLastLoginTime());
        userWithoutSensitiveDto.setJwt(user.getJwt());
        return userWithoutSensitiveDto;
    }
}
