package com.madadipouya.cisapify.app.playlist.controller;

import com.madadipouya.cisapify.app.playlist.model.Playlist;
import com.madadipouya.cisapify.app.playlist.service.PlaylistService;
import com.madadipouya.cisapify.app.song.service.SongService;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    private final SongService songService;

    public PlaylistController(PlaylistService playlistService, UserService userService, SongService songService) {
        this.playlistService = playlistService;
        this.userService = userService;
        this.songService = songService;
    }

    @GetMapping("/playlists")
    public String showPlaylists(Model model) {
        Set<Playlist> playLists = playlistService.getPlaylist(userService.getLoggedInUser().orElse(new User()));
        if (playLists.size() > 0) {
            model.addAttribute("playlists", playLists.stream().map(playlist ->
                    new PlaylistShowCommand(playlist.getName(), playlist.getId(), playlist.getSongs().size()))
                    .collect(Collectors.toList()));
        } else {
            model.addAttribute("message", "You do not have any playlist. " +
                    "To create click <a href='/user/playlists/create'>here</a>");
        }
        return "app/playlist/playlist.html";
    }

    @GetMapping("/playlists/{playlistId}")
    public ModelAndView getPlaylistSongs(@PathVariable long playlistId) {
        return new ModelAndView(String.format("redirect:/user/player_old?playlist=%s", playlistId));
    }

    @GetMapping("/playlists/create")
    public String showCreatePlaylist(Model model) {
        model.addAllAttributes(Map.of("command", new PlayListCommand(),
                "allUserSongs", songService.getAllForCurrentUser()));
        return "app/playlist/playlist_create.html";
    }

    @PostMapping("/playlists/create")
    public ModelAndView createPlaylist(@ModelAttribute("command") PlayListCommand command) {
        playlistService.create(command.playlistSongs, command.getPlaylistName());
        return new ModelAndView("redirect:/user/playlists");
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

    public static class PlaylistShowCommand {

        private static final String URI_PATH = "/user/playlists/%s";

        private final String name;

        private final String uri;

        private final long count;

        PlaylistShowCommand(String name, long id, long count) {
            this.name = name;
            this.uri = String.format(URI_PATH, id);
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public String getUri() {
            return uri;
        }

        public long getCount() {
            return count;
        }
    }
}
