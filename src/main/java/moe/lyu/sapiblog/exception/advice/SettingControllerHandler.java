package moe.lyu.sapiblog.exception.advice;

import moe.lyu.sapiblog.controller.SettingController;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.exception.SettingNotExistException;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = SettingController.class)
@Order(0)
public class SettingControllerHandler {

    @ExceptionHandler(SettingNotExistException.class)
    public Resp handleSettingNotExistException(SettingNotExistException e) {
        return Resp.error(-404, e.getMessage());
    }
}
