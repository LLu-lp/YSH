package com.haowu.shop.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // 获取签名密钥
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 生成JWT令牌
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getSigningKey())
                .compact();
    }

    // 解析JWT令牌
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 从令牌中获取subject
    public String getSubject(String token) {
        return parseToken(token).getSubject();
    }

    // 从令牌中获取自定义声明
    public <T> T getClaim(String token, String key, Class<T> clazz) {
        return clazz.cast(parseToken(token).get(key));
    }

    // 检查令牌是否过期
    public boolean isTokenExpired(String token) {
        return parseToken(token).getExpiration().before(new Date());
    }
}