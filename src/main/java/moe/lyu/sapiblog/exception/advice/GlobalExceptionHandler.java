package moe.lyu.sapiblog.exception.advice;

import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.exception.UserJwtVerifyFailedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public Resp noResourceFoundException(NoResourceFoundException e) {
        return Resp.error(-404, "API you requested is not found");
    }

    @ExceptionHandler(UserJwtVerifyFailedException.class)
    public Resp authCheckFailed(UserJwtVerifyFailedException e) {
        return Resp.error(-403, "Not authorized");
    }

    @ExceptionHandler(value = Exception.class)
    public Resp handleUnknownException(Exception e) {
        e.printStackTrace();
        return Resp.error(-500, "Unknown Exception");
    }

}
