package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import moe.lyu.sapiblog.dto.UserWithoutSensitiveDto;
import moe.lyu.sapiblog.entity.User;
import moe.lyu.sapiblog.exception.UserLoginFailed;
import moe.lyu.sapiblog.mapper.UserMapper;
import moe.lyu.sapiblog.mapper.UserToUserWithoutSensitiveDtoMapper;
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

    public UserWithoutSensitiveDto login(String username, String password) throws NoSuchAlgorithmException {
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

        UserWithoutSensitiveDto userWithoutSensitiveDto = new UserWithoutSensitiveDto();
        return UserToUserWithoutSensitiveDtoMapper.userToUserWithoutSensitiveDto(userDb);
    }

    public Boolean register(User user) {
        return userMapper.insert(user) == 1;
    }

    public Boolean update(User user) {
        return userMapper.updateById(user) == 1;
    }
}
