package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.lyu.sapiblog.dto.UserWithoutSensitiveDto;
import moe.lyu.sapiblog.entity.User;
import moe.lyu.sapiblog.exception.UserJwtVerifyFailedException;
import moe.lyu.sapiblog.exception.UserLoginFailed;
import moe.lyu.sapiblog.mapper.UserMapper;
import moe.lyu.sapiblog.mapper.UserToUserWithoutSensitiveDtoMapper;
import moe.lyu.sapiblog.utils.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UserService {

    UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserWithoutSensitiveDto login(String username, String password) throws NoSuchAlgorithmException, UserLoginFailed {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, username);
        User userDb = userMapper.selectOne(lambdaQueryWrapper);
        if (userDb == null) {
            throw new UserLoginFailed("Username is invalid");
        }
        String pwd = userDb.getPassword();
        userDb.setPassword(password, userDb.getSalt());
        if (!userDb.getPassword().equals(pwd)) {
            throw new UserLoginFailed("Username or password is invalid");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String payload;
        try {
            payload = objectMapper.writeValueAsString(userDb);
        } catch (Exception e) {
            throw new UserLoginFailed("Username or password is invalid");
        }

        User user = new User();
        user.setLastLoginTime();
        user.setId(userDb.getId());
        user.setJwt(payload);
        userMapper.updateById(user);

        return UserToUserWithoutSensitiveDtoMapper.userToUserWithoutSensitiveDto(userDb);
    }

    public Boolean register(User user) {
        return userMapper.insert(user) == 1;
    }

    public Boolean update(User user) {
        return userMapper.updateById(user) == 1;
    }

    public UserWithoutSensitiveDto jwtDbVerify(String token) throws UserJwtVerifyFailedException {

        UserWithoutSensitiveDto userWithoutSensitiveDto = jwtDbVerify(token);

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getJwt, token).eq(User::getId, userWithoutSensitiveDto.getId());

        User user = userMapper.selectOne(lambdaQueryWrapper);
        if (user == null) {
            throw new UserJwtVerifyFailedException("Jwt not exist");
        }

        return UserToUserWithoutSensitiveDtoMapper.userToUserWithoutSensitiveDto(user);
    }

    public UserWithoutSensitiveDto jwtVerify(String token) throws UserJwtVerifyFailedException {
        String payload = Jwt.decodeJwt(token);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(payload, UserWithoutSensitiveDto.class);
        } catch (JsonProcessingException e) {
            throw new UserJwtVerifyFailedException("Jwt decode failed");
        }
    }
}
