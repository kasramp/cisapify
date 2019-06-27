package com.madadipouya.cisapify.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Path;

@Component
public class ApplicationContextUtil {

    private static final String STATIC_SONGS_DIR_PATH = "/static/songs/";

    @Value("${application.name}")
    private String applicationName;

    public String getBaseUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }

    public String getApplicationName() {
        return applicationName;
    }

    public static Path getStaticSongResourcePath() {
        return Path.of(getRootPath(), STATIC_SONGS_DIR_PATH);
    }

    private static String getRootPath() {
        Path rootPath = Path.of(System.getProperty("user.dir"));
        return rootPath.endsWith("target") ? rootPath.getParent().toString() : rootPath.toString();
    }
}
