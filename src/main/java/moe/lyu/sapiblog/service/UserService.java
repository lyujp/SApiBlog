package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.lyu.sapiblog.dto.UserWithoutSensitiveDto;
import moe.lyu.sapiblog.entity.User;
import moe.lyu.sapiblog.exception.UserJwtVerifyFailedException;
import moe.lyu.sapiblog.exception.UserLoginFailed;
import moe.lyu.sapiblog.exception.UserRegisterFailedException;
import moe.lyu.sapiblog.exception.UserUpdateFailedException;
import moe.lyu.sapiblog.mapper.UserMapper;
import moe.lyu.sapiblog.utils.Jwt;
import org.springframework.beans.BeanUtils;
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

    private static UserWithoutSensitiveDto userToUserWithoutSensitiveDto(User user) {
        UserWithoutSensitiveDto userWithoutSensitiveDto = new UserWithoutSensitiveDto();
        BeanUtils.copyProperties(user, userWithoutSensitiveDto);
        return userWithoutSensitiveDto;
    }

    public UserWithoutSensitiveDto login(String username, String password, String totp) throws NoSuchAlgorithmException, UserLoginFailed {
        if(username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new UserLoginFailed("Username or password is empty");
        }

        LambdaQueryChainWrapper<User> userLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(userMapper);
        User userDb = userLambdaQueryChainWrapper.eq(User::getUsername, username).one();
        if(userDb == null) {
            throw new UserLoginFailed("Username is invalid");
        }

        String pwd = userDb.getPassword();
        userDb.setPassword(password, userDb.getSalt());
        if (!userDb.getPassword().equals(pwd)) {
            throw new UserLoginFailed("Username or password is invalid");
        }

        if(!userDb.getTotp().isEmpty() && !userDb.getTotp().equals(totp)) {
            throw new UserLoginFailed("Totp is invalid");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String payload;
        try {
            payload = objectMapper.writeValueAsString(userDb);
        } catch (Exception e) {
            throw new UserLoginFailed("Username or password is invalid");
        }
        userDb.setLastLoginTime();
        LambdaUpdateChainWrapper<User> userLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(userMapper);
        userLambdaUpdateChainWrapper
                .set(User::getId, userDb.getId())
                .set(User::getJwt,payload)
                .set(User::getLastLoginTime,userDb.getLastLoginTime())
                .eq(User::getId, userDb.getId())
                .update();
        return userToUserWithoutSensitiveDto(userDb);
    }

    public void register(User user) throws UserRegisterFailedException {
        LambdaUpdateChainWrapper<User> userLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(userMapper);
        boolean update = userLambdaUpdateChainWrapper.setEntity(user).update();
        if(!update) {
            throw new UserRegisterFailedException("Register failed");
        }

    }

    public void update(User user) throws UserUpdateFailedException {
        if(userMapper.updateById(user) == 0){
            throw new UserUpdateFailedException("");
        }
    }

    public UserWithoutSensitiveDto jwtDbVerify(String token) throws UserJwtVerifyFailedException {

        UserWithoutSensitiveDto userWithoutSensitiveDto = jwtDbVerify(token);

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getJwt, token).eq(User::getId, userWithoutSensitiveDto.getId());

        User user = userMapper.selectOne(lambdaQueryWrapper);
        if (user == null) {
            throw new UserJwtVerifyFailedException("Jwt not exist");
        }

        return userToUserWithoutSensitiveDto(user);
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
