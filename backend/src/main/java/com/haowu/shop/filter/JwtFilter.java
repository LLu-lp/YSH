package com.haowu.shop.filter;

import com.haowu.shop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                if (!jwtUtil.isTokenExpired(token)) {
                    String role = jwtUtil.getClaim(token, "role", String.class);
                    Long userId = null;
                    
                    System.out.println("JWT解析 - 角色: " + role);
                    
                    // 根据角色获取对应的ID，处理可能的类型转换
                    if ("user".equals(role)) {
                        Object userIdObj = jwtUtil.getClaim(token, "userId", Object.class);
                        userId = convertToLong(userIdObj);
                        System.out.println("JWT解析 - 用户ID: " + userId);
                    } else if ("merchant".equals(role)) {
                        Object merchantIdObj = jwtUtil.getClaim(token, "merchantId", Object.class);
                        userId = convertToLong(merchantIdObj);
                        System.out.println("JWT解析 - 商家ID: " + userId);
                    } else if ("admin".equals(role)) {
                        Object adminIdObj = jwtUtil.getClaim(token, "adminId", Object.class);
                        userId = convertToLong(adminIdObj);
                        System.out.println("JWT解析 - 管理员ID: " + userId);
                    }
                    
                    if (userId != null) {
                        // 角色需要加 ROLE_ 前缀
                        String authority = "ROLE_" + role.toUpperCase();
                        System.out.println("JWT解析 - 设置权限: " + authority);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userId, null, Collections.singletonList(new SimpleGrantedAuthority(authority)));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("JWT解析 - 认证成功");
                    } else {
                        System.err.println("JWT解析 - userId为null");
                    }
                }
            } catch (Exception e) {
                // 令牌无效，不做处理
                System.err.println("JWT解析失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
        chain.doFilter(request, response);
    }
    
    private Long convertToLong(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Long) {
            return (Long) obj;
        }
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        }
        if (obj instanceof String) {
            return Long.parseLong((String) obj);
        }
        return Long.parseLong(obj.toString());
    }
}