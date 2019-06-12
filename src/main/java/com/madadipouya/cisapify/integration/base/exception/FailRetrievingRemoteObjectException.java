package com.madadipouya.cisapify.integration.base.exception;

public class FailRetrievingRemoteObjectException extends Exception {

    public FailRetrievingRemoteObjectException(String message) {
        super(message);
    }

    public FailRetrievingRemoteObjectException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
