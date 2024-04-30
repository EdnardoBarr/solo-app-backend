package ednardo.api.soloapp.model.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import io.jsonwebtoken.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import ednardo.api.soloapp.exception.JWTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtils {

    @Value("${solo.app.jwtSecret}")
    private String SECRET_KEY;

    @Value("${solo.app.jwtExpirationSec}")
    private int EXPIRATION_SEC;

    @Value("${solo.app.jwtIssuer}")
    private String ISSUER;

    private Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    public String generateToken(UserDetails user) {
        try {
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withIssuedAt(creationDate())
                    .withExpiresAt(expirationDate())
                    .withSubject(user.getUsername())
                    .sign(algorithm);
        } catch (JWTException exception) {
            throw new JWTException("Invalid token.");
        }
    }

    public String getSubjectFromToken (String token) {
        try {
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTException exception) {
            throw new JWTException("Invalid/expired token.");
        }
    }

    public boolean validateJwtToken(String token) {
        try {
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error("Invalid JWT: {}", e.getMessage());
        }
        return false;
    }

    private Date creationDate() {
        LocalDateTime now = LocalDateTime.now();
        return Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
       // return ZonedDateTime.now(ZoneId.of("America/Recife")).toInstant();
    }

    private Date expirationDate() {
        LocalDateTime now = LocalDateTime.now();
        return Date.from(now.plusSeconds(EXPIRATION_SEC).atZone(ZoneId.systemDefault()).toInstant());
        //return ZonedDateTime.now(ZoneId.of("America/Recife")).plusHours(4).toInstant();
    }






}
