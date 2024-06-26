package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class jwtAuthenFilter extends UsernamePasswordAuthenticationFilter {

    private Logger log = LoggerFactory.getLogger(jwtAuthenFilter.class);

    private AuthenticationManager authenticationManager;

    public jwtAuthenFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            User creds = new ObjectMapper().readValue(req.getInputStream(), User.class);

        if (creds == null || creds.getUsername() == null || creds.getPassword() == null ||
                creds.getUsername().isEmpty() || creds.getPassword().isEmpty()) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        creds.getUsername(),
                        creds.getPassword(),
                        new ArrayList<>() // Optionally, specify the type here
                )
        );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        if (auth != null && auth.isAuthenticated()) {
            String username = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
            String token = JWT.create()
                    .withSubject(username)
                    .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                    .sign(HMAC512(SecurityConstants.SECRET.getBytes()));
            res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
            log.info("[LOGIN] [Success] for user: " + username);
        } else {
            log.warn("[LOGIN] [Failed] Authentication object is null or not authenticated");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("Authentication failed");
        }
    }
}