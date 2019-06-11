package com.madadipouya.cisapify.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class ApplicationContextUtil {

    @Value("${application.name}")
    private String applicationName;

    public String getBaseUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }

    public String getApplicationName() {
        return applicationName;
    }
}
