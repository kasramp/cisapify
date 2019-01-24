package com.madadipouya.cisapify.app.playlist.service.impl;

import com.madadipouya.cisapify.app.playlist.exception.AnonymousUserPlaylistCreationException;
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
    public void create(Set<Song> songs, User user) {
        Playlist playlist = new Playlist();
        playlist.setSongs(songs);
        playlist.setUser(user);
        playlistRepository.save(playlist);
    }

    @Override
    public void create(Set<Song> songs) throws AnonymousUserPlaylistCreationException {
        User user = userService.getLoggedInUser()
                .orElseThrow(() -> new AnonymousUserPlaylistCreationException("Unable to create playlist for anonymous user"));
        create(songs, user);
    }

    @Override
    public Set<Playlist> getPlaylist(User user) {
        return playlistRepository.getByUser(user);
    }

    @Override
    public void delete(Long playlistId) {
        playlistRepository.deleteById(playlistId);
    }

    @Override
    public void update(Playlist playList) {
        playlistRepository.save(playList);
    }
}
