package com.madadipouya.cisapify.app.playlist.controller;

import com.madadipouya.cisapify.app.playlist.model.Playlist;
import com.madadipouya.cisapify.app.playlist.service.PlaylistService;
import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.song.service.SongService;
import com.madadipouya.cisapify.app.upload.service.UploadService;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class PlaylistController {

    private final PlaylistService playlistService;

    private final UserService userService;

    @Autowired
    private SongService songService;

    @Autowired
    private UploadService uploadService;

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


    @GetMapping("/playlists/create")
    public String showCreatePlaylist(Map<String, Object> model, Authentication authentication) {
        model.put("allUserSongs", uploadService.loadAllForUserEmail(authentication.getName()));
        model.put("command", new PlayListCommand());
        return "app/player/playlist_create.html";
    }

    @PostMapping("/playlists/create")
    public ModelAndView createPlaylist(@ModelAttribute("command") PlayListCommand command) {
        Playlist playlist = new Playlist();
        playlist.setName(command.getPlaylistName());
        playlist.setSongs(Arrays.stream(command.getPlaylistSongs()).map(songService::findById).collect(Collectors.toSet()));
        playlist.setUser(userService.getLoggedInUser().get());
        playlistService.create(playlist);
        return new ModelAndView("redirect:/user/playlists");
    }

    public static class PlayListCommand {
        private String playlistName;

        private Long[] playlistSongs;

        public String getPlaylistName() {
            return playlistName;
        }

        public void setPlaylistName(String playlistName) {
            this.playlistName = playlistName;
        }

        public Long[] getPlaylistSongs() {
            return playlistSongs;
        }

        public void setPlaylistSongs(Long[] playlistSongs) {
            this.playlistSongs = playlistSongs;
        }
    }


    private String constructPlaylistUri(Playlist playlist) {
        return String.format("/user/playlists/%s", playlist.getId());
    }
}
