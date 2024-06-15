package com.blubank.doctorappointment.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Shahryar Safizadeh
 * @since 6/15/2024 
 */
@Component
public class JWTUtil {

    @Value("${jwt_secret}")
    private String secret;

    @Value("${jwt_expiration}")
    private long expirationTime;

    public String generateToken(String username) throws IllegalArgumentException, JWTCreationException {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);
        return JWT.create()
                .withSubject("User Details")
                .withClaim("username", username)
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .withIssuer("APPOINTMENTSYSTEM/PROJECT/BLUBANK")
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateTokenAndRetrieveSubject(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User Details")
                .withIssuer("APPOINTMENTSYSTEM/PROJECT/BLUBANK")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("username").asString();
    }

}
