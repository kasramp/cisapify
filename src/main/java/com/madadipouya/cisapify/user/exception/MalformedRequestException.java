package com.madadipouya.cisapify.user.exception;

import org.springframework.security.core.AuthenticationException;

public class MalformedRequestException extends AuthenticationException {

    public MalformedRequestException(String message) {
        super(message);
    }
}
