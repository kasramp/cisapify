package com.madadipouya.cisapify.app.playlist.service;

import com.madadipouya.cisapify.app.playlist.exception.AnonymousUserPlaylistCreationException;
import com.madadipouya.cisapify.app.playlist.model.Playlist;
import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.user.model.User;

import java.util.Set;

public interface PlaylistService {

    void create(String playlistName, Set<Song> songs, User user);

    void create(String playlistName, Set<Song> songs) throws AnonymousUserPlaylistCreationException;

    void create(Playlist playList);

    Set<Playlist> getPlaylist(User user);

    void delete(Long playlistId);

    void update(Playlist playList);
}
