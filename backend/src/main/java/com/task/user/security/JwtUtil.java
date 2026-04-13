package com.task.user.security;

import java.security.Key;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // 🔐 Secret key (must be at least 32 chars)
    private final Key key = Keys.hmacShaKeyFor("secretkeysecretkeysecretkey123456".getBytes());

    // ✅ Generate Token
    public String generateToken(String username, List<String> roles) {

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }

    // ✅ Extract Username
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // ✅ Validate Token
    public boolean validateToken(String token) {
        return getClaims(token).getExpiration().after(new Date());
    }

    // ✅ Extract Claims (FIXED METHOD)
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()   // 🔥 FIXED HERE
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}