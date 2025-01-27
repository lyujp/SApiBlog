package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import moe.lyu.sapiblog.dto.JwtDto;
import moe.lyu.sapiblog.entity.User;
import moe.lyu.sapiblog.exception.UserJwtVerifyFailedException;
import moe.lyu.sapiblog.exception.UserLoginFailedException;
import moe.lyu.sapiblog.exception.UserRegisterFailedException;
import moe.lyu.sapiblog.exception.UserUpdateFailedException;
import moe.lyu.sapiblog.mapper.UserMapper;
import moe.lyu.sapiblog.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService {

    UserMapper userMapper;
    JwtUtils jwtUtils;

    @Autowired
    public UserService(UserMapper userMapper, JwtUtils jwtUtils) {
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
    }

    public User login(String username, String password, String totp) throws NoSuchAlgorithmException, UserLoginFailedException {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new UserLoginFailedException("Username or password is empty");
        }
        username = username.toLowerCase().trim();
        LambdaQueryChainWrapper<User> userLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(userMapper);
        User userDb = userLambdaQueryChainWrapper.eq(User::getUsername, username).one();
        if (userDb == null) {
            throw new UserLoginFailedException("Username is invalid");
        }

        String pwd = userDb.getPassword();
        String pwdGenerate = generatePassword(password, userDb.getSalt());
        System.out.println(pwd);
        System.out.println(pwdGenerate);
        if (!pwdGenerate.equals(pwd)) {
            throw new UserLoginFailedException("Username or password is invalid");
        }

        userDb.setLastLoginTime();
        userDb.setSalt("");
        userDb.setTotp("");
        userDb.setPassword("");

        JwtDto jwtDto = new JwtDto();
        jwtDto.setUsername(userDb.getUsername());
        jwtDto.setRole(userDb.getRole());
        jwtDto.setUserId(userDb.getId());

        try {
            String payload = jwtUtils.generateJwt(jwtDto);
            userDb.setJwt(payload);
        } catch (Exception e) {
            throw new UserLoginFailedException("Username or password is invalid");
        }


        LambdaUpdateChainWrapper<User> userLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(userMapper);
        userLambdaUpdateChainWrapper
                .set(User::getId, userDb.getId())
                .set(User::getJwt, userDb.getJwt())
                .set(User::getLastLoginTime, userDb.getLastLoginTime())
                .eq(User::getId, userDb.getId())
                .update();
        return userDb;
    }

    public void register(User user) throws UserRegisterFailedException, NoSuchAlgorithmException {
        if (user == null) {
            throw new UserRegisterFailedException("Username or password is empty");
        }
        if (user.getId() != null) {
            throw new UserRegisterFailedException("Register failed");
        }
        if (user.getUsername() == null || user.getPassword() == null || user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
            throw new UserRegisterFailedException("Username or password is empty");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new UserRegisterFailedException("Email is empty");
        }
        if (user.getNickname() == null || user.getNickname().isEmpty()) {
            throw new UserRegisterFailedException("Nickname is empty");
        }

        if (!checkUsername(user.getUsername())) {
            throw new UserRegisterFailedException("Username is already in use");
        }

        if (!checkEmail(user.getEmail())) {
            throw new UserRegisterFailedException("Email is already in use");
        }
        String salt = UUID.randomUUID().toString();
        user.setSalt(salt);
        user.setPassword(generatePassword(user.getPassword(), salt));
        user.setLastLoginTime();
        int insert = userMapper.insert(user);
        if (insert == 0) {
            throw new UserRegisterFailedException("Register failed");
        }
    }

    public Boolean checkUsername(String username) {
        LambdaQueryChainWrapper<User> userLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(userMapper);
        User userDb = userLambdaQueryChainWrapper.eq(User::getUsername, username).one();
        return userDb == null;
    }

    public Boolean checkEmail(String email) {
        LambdaQueryChainWrapper<User> userLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(userMapper);
        User userDb = userLambdaQueryChainWrapper.eq(User::getEmail, email).one();
        return userDb == null;
    }

    public Boolean checkPhoneNumber(String phoneNumber) {
        LambdaQueryChainWrapper<User> userLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(userMapper);
        User userDb = userLambdaQueryChainWrapper.eq(User::getPhone, phoneNumber).one();
        return userDb == null;
    }

    public void update(User user, String jwt) throws UserUpdateFailedException, NoSuchAlgorithmException {
        JwtDto userInToken = jwtDbVerify(jwt);
        if (userInToken == null || !Objects.equals(userInToken.getUserId(), user.getId())) {
            throw new UserUpdateFailedException("You can only update your own user");
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String salt = UUID.randomUUID().toString();
            user.setSalt(salt);
            user.setPassword(generatePassword(user.getPassword(), salt));
        }
        if (userMapper.updateById(user) == 0) {
            throw new UserUpdateFailedException("");
        }
    }

    public JwtDto jwtDbVerify(String token) throws UserJwtVerifyFailedException {

        if (token == null || token.isEmpty()) {
            throw new UserJwtVerifyFailedException("Token is empty");
        }

        JwtDto jwtDto = jwtUtils.decodeJwt(token);

        if (jwtDto == null) {
            throw new UserJwtVerifyFailedException("Jwt verify failed");
        }

        LambdaQueryChainWrapper<User> userLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(userMapper);
        User user = userLambdaQueryChainWrapper.eq(User::getJwt, token).eq(User::getId, jwtDto.getUserId()).one();
        if (user == null) {
            throw new UserJwtVerifyFailedException("Jwt not exist");
        }

        return jwtDto;
    }

    public JwtDto jwtVerify(String token) throws UserJwtVerifyFailedException {
        return jwtUtils.decodeJwt(token);
    }


    private String generatePassword(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(password.getBytes(StandardCharsets.UTF_8));
        digest.update(salt.getBytes(StandardCharsets.UTF_8));
        return Arrays.toString(digest.digest());
    }
}
