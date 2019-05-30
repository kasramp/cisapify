package com.madadipouya.cisapify.app.storage.store.exception;

public class StoreFileNotFoundException extends StoreException {

    public StoreFileNotFoundException(String message) {
        super(message);
    }

    public StoreFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StoreFileNotFoundException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}