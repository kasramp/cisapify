package com.madadipouya.cisapify.user.service;

import com.madadipouya.cisapify.user.exception.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;

public interface JwtService {

    Authentication authenticate(HttpServletRequest request) throws AuthenticationException;

    Authentication authenticate(String emailAddress, String password) throws AuthenticationException;

    String createToken(String emailAddress);

    String getTokenHeaderName();

    String getTokenPrefix();

    Authentication getAuthentication(String token) throws UserNotFoundException;

    String getEmailAddress(String token);

    String resolveToken(HttpServletRequest request);

    boolean isValidToken(String token);
}
