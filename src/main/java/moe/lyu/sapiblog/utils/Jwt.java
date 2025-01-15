package moe.lyu.sapiblog.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

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
            verifier.verify(jwt);
            DecodedJWT decodedJWT = JWT.decode(jwt);
            return decodedJWT.getPayload();
        } catch (Exception e) {
            return null;
        }

    }
}
