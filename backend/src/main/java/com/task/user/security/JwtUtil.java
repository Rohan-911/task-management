package com.task.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final Key key = Keys.hmacShaKeyFor("secretkeysecretkeysecretkey123456".getBytes());

   
    public String generateToken(String username, List<String> roles) {

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }

    
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    
    public boolean validateToken(String token) {
        return getClaims(token).getExpiration().after(new Date());
    }

    
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()   
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}