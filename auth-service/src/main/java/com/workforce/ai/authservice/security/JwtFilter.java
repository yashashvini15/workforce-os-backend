package com.workforce.ai.authservice.security;

import com.workforce.ai.authservice.service.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SessionService sessionService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();

        // PUBLIC APIs - JWT CHECK MAT KARO
        if (path.equals("/api/auth/login")
                || path.equals("/api/auth/signup")
                || path.equals("/api/auth/forgot-password")
                || path.equals("/api/auth/reset-password")
                || path.equals("/api/auth/verify-otp")
                || path.startsWith("/oauth2/")
                || path.startsWith("/login/oauth2/")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs")) {

            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        System.out.println("HEADER RECEIVED: " + authHeader);

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);
            try{
                if(jwtUtil.isTokenValid(token)){
                    String tokenId = jwtUtil.extractTokenId(token);

                    if(sessionService.isSessionValid(tokenId)){
                        String email = jwtUtil.extractEmail(token);
                        String role = jwtUtil.extractRole(token);

                        String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                        List<GrantedAuthority> authorities =
                                Collections.singletonList(new SimpleGrantedAuthority(roleName));

                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(email,null,authorities);

                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }catch (Exception e){
                System.out.println("JWT ERROR: "+e.getMessage());
            }
        }
        filterChain.doFilter(request,response);
    }
}