package com.madadipouya.cisapify.integration.gitlab.remote.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponse {

    private long id;

    private String name;

    @JsonProperty("username")
    private String handle;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }
}
