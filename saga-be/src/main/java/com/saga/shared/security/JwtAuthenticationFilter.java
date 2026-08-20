package com.saga.shared.security;

import com.saga.auth.service.JwtProviderService;
import com.saga.auth.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProviderService jwtProviderPort;
    private final TokenBlacklistService tokenBlacklistPort;

    public JwtAuthenticationFilter(JwtProviderService jwtProviderPort, TokenBlacklistService tokenBlacklistPort) {
        this.jwtProviderPort = jwtProviderPort;
        this.tokenBlacklistPort = tokenBlacklistPort;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (tokenBlacklistPort.isBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"message\": \"Token has been invalidated\"}");
                return;
            }

            try {
                if (jwtProviderPort.validateToken(token)) {
                    String email = jwtProviderPort.getEmailFromToken(token);
                    String roleStr = jwtProviderPort.getRoleFromToken(token);
                    
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleStr);
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));
                            
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}