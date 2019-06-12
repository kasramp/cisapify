package com.madadipouya.cisapify.integration.gitlab;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.integration.base.SongAware;
import com.madadipouya.cisapify.integration.gitlab.exception.FailRetrievingRemoteObjectException;

import java.io.IOException;
import java.nio.file.Path;

public interface GitLabIntegration extends SongAware {

    String getUserHandle(String token);

    Path loadRemoteSong(String token, Song song) throws IOException, FailRetrievingRemoteObjectException;
}