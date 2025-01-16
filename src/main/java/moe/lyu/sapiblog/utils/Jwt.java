package moe.lyu.sapiblog.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import moe.lyu.sapiblog.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Consumer;

public class Jwt {

    @Value("${jwt.sk}")
    private static String sk;


    public static String generateJwt(String payload) {
        Algorithm algorithm = Algorithm.HMAC512(sk);
        return JWT.create()
                .withPayload(payload)
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000 * 24 * 10))
                .sign(algorithm);
    }

    public static String decodeJwt(String jwt) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(sk);
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(jwt).getPayload();
        } catch (Exception e) {
            return null;
        }
    }
}
