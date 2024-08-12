package com.stylish.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expiration}")
    public long defaultExpire;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Jwts.SIG.HS256.key()
                .random(new SecureRandom(SECRET.getBytes(StandardCharsets.UTF_8)))
                .build();
    }

    public String createToken(String username, Map<String, Object> claims) {
        return createToken(username, claims, defaultExpire);
    }

    public String createToken(String username, Map<String, Object> claims, Long expire) {
        Date now = new Date();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer("Stylish")
                .claims(claims)
                .subject(username) //set "stylishtest_abc@test.com"
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expire))
                .signWith(key)
                .header().add("typ", "JWT")
                .and()
                .compact();
        // create and return new JWT token
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            logger.error("Token already expired", e);
            throw new RuntimeException("Token already expired");
        } catch (JwtException e) {
            logger.error("Token is invalid", e);
            throw new RuntimeException("Token is invalid");
        } catch (Exception e) {
            logger.error("Token parsing failed", e);
            throw new RuntimeException("Token parsing failed");
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }
}