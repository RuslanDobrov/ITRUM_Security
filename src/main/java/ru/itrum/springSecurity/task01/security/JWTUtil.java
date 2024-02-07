package ru.itrum.springSecurity.task01.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt_secret}")
    private String secret;

    @Value("${jwt_expiration_time}")
    private int expirationSeconds;

    public String generateToken(String username) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusSeconds(expirationSeconds).toInstant());
        return JWT.create()
                .withSubject("User details")
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withIssuer("dobrov")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateToken(String token) throws JWTVerificationException {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                    .withSubject("User details")
                    .withIssuer("dobrov")
                    .build();

            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("username").asString();
        } catch (TokenExpiredException e) {
            throw new TokenExpiredException("Token has expired");
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Token is invalid");
        }
    }
}