package moe.lyu.sapiblog.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.lyu.sapiblog.dto.JwtDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.sk}")
    private String sk;


    public String generateJwt(JwtDto payload) throws JsonProcessingException {
        Algorithm algorithm = Algorithm.HMAC512(sk);
        return JWT.create()
                .withPayload(new ObjectMapper().writeValueAsString(payload))
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000 * 24 * 10))
                .sign(algorithm);
    }

    public JwtDto decodeJwt(String jwt) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(sk);
            String payload = JWT
                    .require(algorithm)
                    .build()
                    .verify(jwt)
                    .getPayload();
            String jwtDtoStr = new String(Base64.getUrlDecoder().decode(payload));
            return new ObjectMapper().readValue(jwtDtoStr, JwtDto.class);
        } catch (Exception e) {
            return null;
        }
    }
}
