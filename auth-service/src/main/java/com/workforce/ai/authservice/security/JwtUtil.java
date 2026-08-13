package com.workforce.ai.authservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email,String role){


        String tokenId = UUID.randomUUID().toString();

        return Jwts.builder()
                .setId(tokenId)
                .setSubject(email)
                .claim("role",role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractTokenId(String token){
        return Jwts.parserBuilder().setSigningKey(getKey()).build()
                .parseClaimsJws(token).getBody().getId();
    }

    public String extractRole(String token){
        return Jwts.parserBuilder().setSigningKey(getKey()).build()
                .parseClaimsJws(token).getBody().get("role", String.class);
    }

    public String extractEmail(String token){
        return Jwts.parserBuilder().setSigningKey(getKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isTokenValid(String token){
        try{
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            return true;
        }catch (JwtException e){
            System.out.println("JWT ERROR: " + e.getMessage());
            return false;
        }
    }
}
