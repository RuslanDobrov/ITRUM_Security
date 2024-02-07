package ru.itrum.springSecurity.task01.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.itrum.springSecurity.task01.controllers.AuthController;
import ru.itrum.springSecurity.task01.security.JWTUtil;
import ru.itrum.springSecurity.task01.services.PersonDetailsService;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final PersonDetailsService personDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractTokenFromRequest(request);

        if (token != null && validateToken(token)) {
            Authentication authentication = createAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return "Missing token in Authorization header";
    }

    private boolean validateToken(String token) {
        try {
            String username = jwtUtil.validateToken(token);
            personDetailsService.loadUserByUsername(username);
            return true;
        } catch (TokenExpiredException e) {
            logger.error("Token has expired", e);
            return false;
        } catch (JWTVerificationException e) {
            logger.error("Invalid token", e);
            return false;
        }
    }

    private Authentication createAuthentication(String token) {
        UserDetails userDetails = extractUserDetailsFromToken(token);
        return new UsernamePasswordAuthenticationToken(userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities());
    }

    private UserDetails extractUserDetailsFromToken(String token) {
        String username = jwtUtil.validateToken(token);
        return personDetailsService.loadUserByUsername(username);
    }
}
