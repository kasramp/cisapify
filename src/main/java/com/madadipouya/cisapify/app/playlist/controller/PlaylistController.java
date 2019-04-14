package com.madadipouya.cisapify.app.playlist.controller;

import com.madadipouya.cisapify.app.playlist.model.Playlist;
import com.madadipouya.cisapify.app.playlist.service.PlaylistService;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
                Collectors.toMap(this::constructPlaylistUri, Playlist::getName)));
        return "app/player/playlist.html";
    }

    @GetMapping("/playlists/{playlistId}")
    public ModelAndView getPlaylistSongs(@PathVariable long playlistId) {
        return new ModelAndView(String.format("redirect:/user/player?playlist=%s", playlistId));
    }

    private String constructPlaylistUri(Playlist playlist) {
        return String.format("/user/playlists/%s", playlist.getId());
    }
}
