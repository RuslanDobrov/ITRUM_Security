package ru.itrum.springSecurity.task01.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.itrum.springSecurity.task01.services.PersonDetailsService;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final PersonDetailsService personDetailsService;
    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String apiPrefix = env.getProperty("api.v1.prefix");

        http.csrf().disable()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(jwtAuthenticationProvider)
                .authorizeRequests()
                .antMatchers("/" + apiPrefix + "/admin/**").hasRole("SUPER_ADMIN")
                .antMatchers("/" + apiPrefix + "/moderator/**").hasAnyRole("SUPER_ADMIN", "MODERATOR")
                .antMatchers("/" + apiPrefix + "/public/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/" + apiPrefix + "/login").permitAll()
                .and()
                .logout().logoutUrl("/" + apiPrefix + "/logout")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(personDetailsService).passwordEncoder(getPasswordEncoder());
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}