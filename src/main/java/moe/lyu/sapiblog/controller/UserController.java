package moe.lyu.sapiblog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import moe.lyu.sapiblog.annotation.AuthCheck;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.dto.UserLoginDto;
import moe.lyu.sapiblog.entity.User;
import moe.lyu.sapiblog.exception.UserLoginFailed;
import moe.lyu.sapiblog.exception.UserUpdateFailedException;
import moe.lyu.sapiblog.service.UserService;
import moe.lyu.sapiblog.utils.JwtUtils;
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
    JwtUtils jwtUtils;

    @Autowired
    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public Resp login(@RequestBody UserLoginDto userLoginDto)
            throws NoSuchAlgorithmException, UserLoginFailed, JsonProcessingException {
        User user =
                userService.login(userLoginDto.getUsername(), userLoginDto.getPassword(), userLoginDto.getTotp());
        return Resp.success(user.getJwt());
    }

    @AuthCheck
    @PostMapping("/update")
    public Resp update(@RequestBody User user, HttpServletRequest request) throws UserUpdateFailedException, NoSuchAlgorithmException {
        String jwt = request.getHeader("Authorization");
        userService.update(user, jwt);
        return Resp.success();
    }
}
