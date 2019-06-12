package com.madadipouya.cisapify.integration.base.exception;

public class SongsListRetrievalException extends Exception {

    public SongsListRetrievalException(String message) {
        super(message);
    }

    public SongsListRetrievalException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
