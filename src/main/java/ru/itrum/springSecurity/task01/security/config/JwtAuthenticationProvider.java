package ru.itrum.springSecurity.task01.security.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.itrum.springSecurity.task01.security.JWTUtil;
import ru.itrum.springSecurity.task01.services.PersonDetailsService;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JWTUtil jwtUtil;
    private final PersonDetailsService personDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationProvider.class);
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();

        try {
            String username = jwtUtil.validateToken(token);
            UserDetails userDetails = personDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
        } catch (JWTVerificationException e) {
            logger.error("Invalid JWT Token", e);
            throw new BadCredentialsException("Invalid JWT Token");
        } catch (UsernameNotFoundException e) {
            logger.error("User not found", e);
            throw new BadCredentialsException("User not found");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}


