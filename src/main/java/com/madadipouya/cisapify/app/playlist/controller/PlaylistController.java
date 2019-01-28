package com.madadipouya.cisapify.app.playlist.controller;

import com.madadipouya.cisapify.app.playlist.model.Playlist;
import com.madadipouya.cisapify.app.playlist.service.PlaylistService;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class PlaylistController {

    private final PlaylistService playlistService;

    private final UserService userService;

    public PlaylistController(PlaylistService playlistService, UserService userService) {
        this.playlistService = playlistService;
        this.userService = userService;
    }

    @GetMapping("/playlists")
    public String showPlaylists(Map<String, Object> model) {
        User user = userService.getLoggedInUser().orElse(new User());
        model.put("playlists", playlistService.getPlaylist(user).stream().collect(
                Collectors.toMap(Playlist::getId, Playlist::getName)));
        return "app/player/playlist.html";
    }
}
