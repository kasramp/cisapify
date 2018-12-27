package com.madadipouya.cisapify.i18n.service.impl;

import com.madadipouya.cisapify.i18n.service.I18nService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class I18nServiceImpl implements I18nService {

    private final MessageSource messageSource;

    public I18nServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, Locale.getDefault());
    }
}
