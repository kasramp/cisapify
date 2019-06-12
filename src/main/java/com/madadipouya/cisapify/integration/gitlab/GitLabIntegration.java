package com.madadipouya.cisapify.integration.gitlab;

import com.madadipouya.cisapify.integration.base.SongAware;

public interface GitLabIntegration extends SongAware {

    String getUserHandle(String token);
}