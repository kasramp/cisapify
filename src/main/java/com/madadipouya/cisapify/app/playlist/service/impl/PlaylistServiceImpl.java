package com.madadipouya.cisapify.app.playlist.service.impl;

import com.madadipouya.cisapify.app.playlist.exception.AnonymousUserPlaylistCreationException;
import com.madadipouya.cisapify.app.playlist.exception.PlaylistNotExistException;
import com.madadipouya.cisapify.app.playlist.model.Playlist;
import com.madadipouya.cisapify.app.playlist.repository.PlaylistRepository;
import com.madadipouya.cisapify.app.playlist.service.PlaylistService;
import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;

    private final UserService userService;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository, UserService userService) {
        this.playlistRepository = playlistRepository;
        this.userService = userService;
    }

    @Override
    public void create(String playlistName, Set<Song> songs, User user) {
        Playlist playlist = new Playlist();
        playlist.setName(playlistName);
        playlist.setSongs(songs);
        playlist.setUser(user);
        playlistRepository.save(playlist);
    }

    @Override
    public void create(String playlistName, Set<Song> songs) throws AnonymousUserPlaylistCreationException {
        User user = userService.getLoggedInUser()
                .orElseThrow(() -> new AnonymousUserPlaylistCreationException("Unable to create playlist for anonymous user"));
        create(playlistName, songs, user);
    }

    @Override
    public void create(Playlist playList) {
        playlistRepository.save(playList);
    }

    @Override
    public Set<Playlist> getPlaylist(User user) {
        return playlistRepository.getByUser(user);
    }

    /*
     Get playlists via User Service to ensure other users won't
     be able to get songs of each other by passing random playlist
      */
    @Override
    public Set<Song> getSongs(long playlistId) throws PlaylistNotExistException {
        return getPlaylist(userService.getLoggedInUser().orElse(new User()))
                .stream()
                .filter(playlist -> playlist.getId() == playlistId)
                .findFirst()
                .orElseThrow(() -> new PlaylistNotExistException(String.format("Playlist %s does not exist", playlistId)))
                .getSongs();
    }

    @Override
    public void delete(long playlistId) {
        playlistRepository.deleteById(playlistId);
    }

    @Override
    public void update(Playlist playList) {
        playlistRepository.save(playList);
    }
}
