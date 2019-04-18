package com.madadipouya.cisapify.app.playlist.controller;

import com.madadipouya.cisapify.app.playlist.model.Playlist;
import com.madadipouya.cisapify.app.playlist.service.PlaylistService;
import com.madadipouya.cisapify.app.upload.service.UploadService;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class PlaylistController {

    private final PlaylistService playlistService;

    private final UserService userService;

    private final UploadService uploadService;

    public PlaylistController(PlaylistService playlistService, UserService userService, UploadService uploadService) {
        this.playlistService = playlistService;
        this.userService = userService;
        this.uploadService = uploadService;
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

    @GetMapping("/playlists/create")
    public String showCreatePlaylist(Map<String, Object> model, Authentication authentication) {
        model.put("allUserSongs", uploadService.loadAllForUserEmail(authentication.getName()));
        model.put("command", new PlayListCommand());
        return "app/player/playlist_create.html";
    }

    @PostMapping("/playlists/create")
    public ModelAndView createPlaylist(@ModelAttribute("command") PlayListCommand command) {
        playlistService.create(command.playlistSongs, command.getPlaylistName());
        return new ModelAndView("redirect:/user/playlists");
    }

    private String constructPlaylistUri(Playlist playlist) {
        return String.format("/user/playlists/%s", playlist.getId());
    }

    public static class PlayListCommand {
        private String playlistName;

        private Set<Long> playlistSongs;

        public String getPlaylistName() {
            return playlistName;
        }

        public void setPlaylistName(String playlistName) {
            this.playlistName = playlistName;
        }

        public Set<Long> getPlaylistSongs() {
            return playlistSongs;
        }

        public void setPlaylistSongs(Set<Long> playlistSongs) {
            this.playlistSongs = playlistSongs;
        }
    }
}
