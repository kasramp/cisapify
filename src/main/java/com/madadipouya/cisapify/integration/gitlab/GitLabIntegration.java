package com.madadipouya.cisapify.integration.gitlab;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.integration.gitlab.exception.FailRetrievingRemoteObjectException;
import com.madadipouya.cisapify.user.model.User;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface GitLabIntegration {

    String getUserHandle(String token);

    List<Song> getSongs(User user);

    Path loadRemoteSong(String token, Song song) throws IOException, FailRetrievingRemoteObjectException;
}