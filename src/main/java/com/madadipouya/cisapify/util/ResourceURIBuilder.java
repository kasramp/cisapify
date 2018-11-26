package com.madadipouya.cisapify.util;

import java.nio.file.Path;

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

        return this;
    }

    public ResourceURIBuilder withParameters(Object... args) {
        this.args = args;
        return this;
    }

    public String build() {
        return "";
    }
}
