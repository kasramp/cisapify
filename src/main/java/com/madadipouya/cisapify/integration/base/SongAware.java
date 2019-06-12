package com.madadipouya.cisapify.integration.base;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.integration.base.exception.FailRetrievingRemoteObjectException;
import com.madadipouya.cisapify.integration.base.exception.SongsListRetrievalException;
import com.madadipouya.cisapify.user.model.User;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface SongAware {

    List<Song> getSongs(User user) throws SongsListRetrievalException;

    Path loadRemoteSong(String token, Song song) throws IOException, FailRetrievingRemoteObjectException;
}