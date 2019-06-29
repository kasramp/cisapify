package com.madadipouya.cisapify.integration.dropbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.madadipouya.cisapify.i18n.service.I18nService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@PropertySource(value = "classpath:dropbox.properties")
public class DropboxProperties {

    private static final Logger logger = LoggerFactory.getLogger(DropboxProperties.class);

    private final ObjectMapper objectMapper;

    private final I18nService i18nService;

    @Value("${app.info.key}")
    private String appInfoKey;

    @Value("${app.info.secret}")
    private String appInfoSecret;

    public DropboxProperties(ObjectMapper objectMapper, I18nService i18nService) {
        this.objectMapper = objectMapper;
        this.i18nService = i18nService;
    }

    public String getAppInfoAsJson() {
        try {
            return objectMapper.writeValueAsString(Map.of("key", appInfoKey, "secret", appInfoSecret));
        } catch (JsonProcessingException exception) {
            logger.error(i18nService.getMessage("dropbox.properties.failedToReadCredentials"), exception);
        }
        return StringUtils.EMPTY;
    }
}
