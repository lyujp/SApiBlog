package moe.lyu.sapiblog.controller;

import jakarta.servlet.http.HttpServletRequest;
import moe.lyu.sapiblog.annotation.AuthCheck;
import moe.lyu.sapiblog.dto.JwtDto;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.dto.UserLoginDto;
import moe.lyu.sapiblog.entity.User;
import moe.lyu.sapiblog.exception.UserLoginFailedException;
import moe.lyu.sapiblog.exception.UserRegisterFailedException;
import moe.lyu.sapiblog.exception.UserUpdateFailedException;
import moe.lyu.sapiblog.service.UserService;
import moe.lyu.sapiblog.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

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
            throws NoSuchAlgorithmException, UserLoginFailedException {
        User user = userService.login(userLoginDto.getUsername(), userLoginDto.getPassword(), userLoginDto.getTotp());
        return Resp.success(user.getJwt());
    }

    @AuthCheck
    @PostMapping("/update")
    public Resp update(@RequestBody User user, HttpServletRequest request) throws UserUpdateFailedException, NoSuchAlgorithmException {
        String jwt = request.getHeader("Authorization");
        JwtDto userDto = userService.jwtDbVerify(jwt);
        if(!Objects.equals(userDto.getUserId(), user.getId())){
            throw new UserUpdateFailedException("Update failed");
        }
        userService.update(user);
        return Resp.success();
    }

    @AuthCheck(jwtDbCheck = true,role = "ADMIN")
    @PostMapping("/register")
    public Resp register(@RequestBody User user) throws UserRegisterFailedException, NoSuchAlgorithmException {
        userService.register(user);
        return Resp.success();
    }

    @GetMapping("/check/username/{username}")
    public Resp checkUsername(@PathVariable String username) {
        Boolean b = userService.checkUsername(username);
        if(b){
            return Resp.success();
        }else{
            return Resp.error(-200,"Username is already taken");
        }
    }

    @GetMapping("/check/email/{email}")
    public Resp checkEmail(@PathVariable String email) {
        Boolean b = userService.checkEmail(email);
        if(b){
            return Resp.success();
        }else{
            return Resp.error(-200,"Email is already taken");
        }
    }
}
