package com.madadipouya.cisapify.util;

import java.nio.file.Path;
import java.util.Objects;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodName;

public class ResourceURIBuilder {

    private final Class<?> clazz;

    private String methodName;

    private String path;

    private Object[] args;

    public ResourceURIBuilder(Class<?> clazz) {
        this.clazz = clazz;
    }

    public ResourceURIBuilder withClearState() {
        methodName = null;
        path = null;
        args = null;
        return this;
    }

    public ResourceURIBuilder withMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public ResourceURIBuilder withPath(Path path) {
        this.path = path.toString();
        return this;
    }

    public ResourceURIBuilder withPath(String path) {
        this.path = path;
        return this;
    }

    public ResourceURIBuilder withParameters(Object... args) {
        this.args = args;
        return this;
    }

    public String build() {
        String builderPath;
        if(isBlank(path)) {
            builderPath = fromMethodName(clazz, methodName, args).build().toString();
        } else if(Objects.isNull(args)) {
            builderPath = fromMethodName(clazz, methodName, encode(path, UTF_8)).build().toString();
        } else {
            builderPath = fromMethodName(clazz, methodName, encode(path, UTF_8), args).build().toString();
        }
        return builderPath;
    }
}
