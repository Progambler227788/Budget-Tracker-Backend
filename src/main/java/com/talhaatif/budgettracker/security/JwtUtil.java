package com.talhaatif.budgettracker.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/*
Generate a JWT token using HMAC SHA256 with a secret key.
Extract the username from the token.
Validate the token for expiration and authenticity.
*/
@Component
public class JwtUtil {

    @Value("${secret.key}")
    private String SECRET_KEY;


    private final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Generate token using username instead of email
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email) // Email
                .claim("role", role ) // Include role as claim
                .setIssuedAt(new Date()) // issue time, creation time
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    //  Extract username from token
    public String extractEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // Validate JWT
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
