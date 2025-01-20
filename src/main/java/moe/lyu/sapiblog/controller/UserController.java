package moe.lyu.sapiblog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.lyu.sapiblog.annotation.AuthCheck;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.dto.UserLoginDto;
import moe.lyu.sapiblog.dto.UserWithoutSensitiveDto;
import moe.lyu.sapiblog.entity.User;
import moe.lyu.sapiblog.exception.UserLoginFailed;
import moe.lyu.sapiblog.exception.UserUpdateFailedException;
import moe.lyu.sapiblog.service.UserService;
import moe.lyu.sapiblog.utils.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Resp login(@RequestBody UserLoginDto userLoginDto)
            throws NoSuchAlgorithmException, UserLoginFailed, JsonProcessingException {
        UserWithoutSensitiveDto userWithoutSensitiveDto =
                userService.login(userLoginDto.getUsername(), userLoginDto.getPassword(), userLoginDto.getTotp());
        String payload = Jwt.generateJwt(new ObjectMapper().writeValueAsString(userWithoutSensitiveDto));
        return Resp.success(payload);
    }

    @AuthCheck
    @PostMapping("/update")
    public Resp update(@RequestBody User user) throws UserUpdateFailedException {
        userService.update(user);
        return Resp.success();
    }
}
