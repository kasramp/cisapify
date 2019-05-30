package com.madadipouya.cisapify.app.storage.store.exception;

public class StoreException extends Exception {

    public StoreException(String message) {
        super(message);
    }

    public StoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
