package com.madadipouya.cisapify.integration.gitlab.impl;

import com.madadipouya.cisapify.integration.gitlab.GitLabIntegration;
import com.madadipouya.cisapify.integration.gitlab.remote.request.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DefaultGitLabIntegration implements GitLabIntegration {

    private static final String BASE_URL = "https://gitlab.com/api/v4/%s";

    private static final String USER_URL = "user";

    private final RestTemplate restTemplate;

    private DefaultGitLabIntegration(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // TODO proper exception handling
    @Override
    public String getUserHandle(String token) {
        return restTemplate.getForEntity(String.format(BASE_URL, USER_URL), UserResponse.class).getBody().getHandle();
    }
}
