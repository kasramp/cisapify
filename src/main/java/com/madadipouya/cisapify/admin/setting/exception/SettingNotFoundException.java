package com.madadipouya.cisapify.admin.setting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class SettingNotFoundException extends Exception {

    public SettingNotFoundException(String message) {
        super(message);
    }
}