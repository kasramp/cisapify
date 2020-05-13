package com.madadipouya.cisapify.user.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.madadipouya.cisapify.user.exception.MalformedRequestException;
import com.madadipouya.cisapify.user.model.Role;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.service.JwtService;
import com.madadipouya.cisapify.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Service
public class DefaultJwtService implements JwtService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultJwtService.class);

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final ObjectMapper objectMapper;

    public DefaultJwtService(UserService userService, AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication authenticate(HttpServletRequest request) throws AuthenticationException {
        try {
            User user = objectMapper.readValue(request.getInputStream(), User.class);
            return authenticate(user.getEmailAddress(), user.getPassword());
        } catch (IOException deserializationException) {
            logger.info("Failed to deserialize authentication request");
            throw new MalformedRequestException("Invalid request");
        }
    }

    @Override
    public Authentication authenticate(String emailAddress, String password) throws AuthenticationException {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(emailAddress, password));
    }

    @Override
    public String createToken(String emailAddress) {
        return userService.getUserByEmailAddress(emailAddress).map(user -> JWT.create().withSubject(user.getEmailAddress())
                .withClaim("roles", user.getRoles().stream().map(Role::getRole) // TODO fix this
                        .collect(Collectors.toList()))
                .withIssuedAt(new Date())
                .withIssuer("Cisapify") // TODO fix this
                .withExpiresAt(new Date(System.currentTimeMillis() + 864_000_000)) // TODO fix this
                .sign(HMAC512("SecretKeyToGenJWTs".getBytes())) // TODO fix this
        ).orElse(StringUtils.EMPTY);
    }

    @Override
    public String getTokenHeaderName() {
        return "Authorization";
    }

    @Override
    public String getTokenPrefix() {
        return "Bearer ";
    }

    @Override
    public Authentication getAuthentication(String token) throws UsernameNotFoundException {
        UserDetails userDetails = constructUserDetails(token);
        return new UsernamePasswordAuthenticationToken(userDetails, StringUtils.EMPTY, userDetails.getAuthorities());
    }

    @Override
    public String getEmailAddress(String token) {
        return JWT.require(Algorithm.HMAC512("SecretKeyToGenJWTs".getBytes())) // TODO fix this later
                .build()
                .verify(token)
                .getSubject();
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(getTokenHeaderName());
        return StringUtils.isNotBlank(header) && header.startsWith(getTokenPrefix()) ? header.substring(7) : StringUtils.EMPTY;
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            return StringUtils.isNotBlank(token) &&
                    JWT.require(Algorithm.HMAC512("SecretKeyToGenJWTs".getBytes())) // TODO fix this later
                            .build()
                            .verify(token) != null;
        } catch (JWTVerificationException verificationException) {
            logger.warn("Provided token {} is invalid", token);
        }
        return false;
    }

    private UserDetails constructUserDetails(String token) {
        String emailAddress = getEmailAddress(token);
        User user = userService.getUserByEmailAddress(emailAddress)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("%s doesn't exist", emailAddress)));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmailAddress())
                .password(user.getPassword())
                .roles(user.getRoles().stream().map(Role::getRole).toArray(String[]::new))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isEnabled())
                .build();
    }
}