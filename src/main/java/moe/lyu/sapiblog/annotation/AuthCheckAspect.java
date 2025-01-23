package moe.lyu.sapiblog.annotation;

import jakarta.servlet.http.HttpServletRequest;
import moe.lyu.sapiblog.dto.JwtDto;
import moe.lyu.sapiblog.exception.UserJwtVerifyFailedException;
import moe.lyu.sapiblog.service.UserService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Component
@Aspect
public class AuthCheckAspect {

    UserService userService;

    @Autowired
    public AuthCheckAspect(UserService userService) {
        this.userService = userService;
    }

    @Before("@annotation(authCheck)")
    public void check(AuthCheck authCheck) throws UserJwtVerifyFailedException {
        if (authCheck.skipCheck()) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String jwt = request.getHeader("Authorization");
        JwtDto user = null;
        if (authCheck.jwtDbCheck()) {
            user = userService.jwtDbVerify(jwt);
        } else {
            user = userService.jwtVerify(jwt);
        }
        if (user == null) {
            throw new UserJwtVerifyFailedException("Jwt is invalid");
        }
    }
}
