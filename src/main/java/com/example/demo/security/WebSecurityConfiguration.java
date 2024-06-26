package com.example.demo.security;


import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;


@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private UserDetailsServiceImpl userDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurityConfiguration(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
          http.cors().and().csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll()
            .antMatchers("/admin/**").hasRole("ADMIN") // Example: Require ADMIN role for /admin/** endpoints
            .anyRequest().authenticated()
            .and()
            .addFilter(new jwtAuthenFilter(authenticationManager()))
            .addFilter(new authencationVertification(authenticationManager()))
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling().accessDeniedHandler((req, res, e) -> {
                res.setContentType("application/json");
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(res.getWriter(), "Access denied message here");
            });
    }


    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
    if (shouldUseStrongPasswordEncoder()) {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(bCryptPasswordEncoder);
    } else {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(NoOpPasswordEncoder.getInstance());
    }
    }

    private boolean shouldUseStrongPasswordEncoder() {
        return true;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    if (shouldEnableCors()) {
        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        config.addAllowedMethod(HttpMethod.PUT);
        config.addAllowedMethod(HttpMethod.DELETE);
        config.addAllowedHeader("Authorization");
        config.setMaxAge(3600L); // 1 hour max age for preflight requests
        source.registerCorsConfiguration("/**", config);
    } else {
        CorsConfiguration config = new CorsConfiguration();
      
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
    }

    return source;
}

    private boolean shouldEnableCors() {
        return Boolean.parseBoolean(System.getenv("ENABLE_CORS"));
    }
}