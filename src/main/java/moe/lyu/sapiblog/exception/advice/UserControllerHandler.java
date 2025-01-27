package moe.lyu.sapiblog.exception.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import moe.lyu.sapiblog.controller.UserController;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.exception.UserLoginFailedException;
import moe.lyu.sapiblog.exception.UserUpdateFailedException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.NoSuchAlgorithmException;

@RestControllerAdvice(assignableTypes = {UserController.class})
@Order(0)
public class UserControllerHandler {

    @ExceptionHandler(UserLoginFailedException.class)
    public Resp handleUserLoginFailed(UserLoginFailedException e) {
        return Resp.error(-200, e.getMessage());
    }

    @ExceptionHandler(UserUpdateFailedException.class)
    public Resp handleUserUpdateFailedException(UserUpdateFailedException e) {
        return Resp.error(-200, e.getMessage());
    }

    @ExceptionHandler(NoSuchAlgorithmException.class)
    public Resp handleNoSuchAlgorithmException(NoSuchAlgorithmException e) {
        return Resp.error(-500, e.getMessage());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public Resp handleJsonProcessingException(JsonProcessingException e) {
        return Resp.error(-500, e.getMessage());
    }

}
