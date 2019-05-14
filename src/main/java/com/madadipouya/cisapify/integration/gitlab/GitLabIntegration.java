package com.madadipouya.cisapify.integration.gitlab;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.integration.gitlab.exception.FailRetrievingRemoteObjectException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface GitLabIntegration {

    String getUserHandle(String token);

    List<Song> getSongs(String token, String handle, String repositoryName);

    Path loadRemoteSong(String token, Song song) throws IOException, FailRetrievingRemoteObjectException;
}
