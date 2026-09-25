package com.hengthay.store.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader("Authorization");
        // If the authHeader not matching
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Do another filter
            filterChain.doFilter(request, response);
            return;
        }

        var tokenString = authHeader.replace("Bearer ", "");
        var jwt = jwtService.parseToken(tokenString);

        // If token not validated
        if(jwt == null || jwt.isExpired()){
            // do another filter
            filterChain.doFilter(request, response);
            return;
        }

        // Create the Spring Security authentication token.
        // Add SimpleGrantedAuthority to tell spring
        // which role able to access protected resource
        var authentication = new UsernamePasswordAuthenticationToken(
                jwt.getUserId(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + jwt.getRole()))
        );

        // Attach web-specific metadata (client IP, session ID) to the token.
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        // Store the authenticated user in the current thread's SecurityContext.
        // we can use it later
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // proceed next filter
        filterChain.doFilter(request, response);
    }
}
