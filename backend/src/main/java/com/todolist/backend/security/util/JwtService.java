package com.todolist.backend.security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.token.duration}")
    private long tokenDuration;

    @Value("${jwt.token.refresh.duration}")
    private long refreshTokenDuration;

    @Value("${jwt.token.issuer}")
    private String issuer;

    private final Algorithm algorithm;

    public JwtService() {
        this.algorithm = Algorithm.HMAC256(secret.getBytes());
    }

    public String createAccessToken(String userId, List<String> roles){
        return JWT.create()
                .withSubject(userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + tokenDuration * 60 * 1000))
                .withIssuer(issuer)
                .withClaim("roles", roles)
                .sign(algorithm);
    }

    public String createRefreshToken(String userId){
        return JWT.create()
                .withSubject(userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenDuration * 60 * 1000))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public DecodedJWT decodeJwt(String token){
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }
}
