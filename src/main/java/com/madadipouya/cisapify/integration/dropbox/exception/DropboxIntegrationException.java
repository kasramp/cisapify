package com.madadipouya.cisapify.integration.dropbox.exception;

public class DropboxIntegrationException extends Exception {

    public DropboxIntegrationException(String message) {
        super(message);
    }

    public DropboxIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}