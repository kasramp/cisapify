package com.madadipouya.cisapify.app.player.controller;

import com.madadipouya.cisapify.app.playlist.exception.PlaylistNotExistException;
import com.madadipouya.cisapify.app.playlist.service.PlaylistService;
import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.song.service.SongService;
import com.madadipouya.cisapify.app.storage.store.exception.StoreException;
import com.madadipouya.cisapify.user.service.UserService;
import com.madadipouya.cisapify.util.ResourceURIBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Controller
@RequestMapping("/user")
public class PlayerController {

    private final String HEADER_VALUE_ATTACHMENT = "attachment; filename=\"%s\"";

    private final PlaylistService playlistService;

    private final ResourceURIBuilder resourceURIBuilder;

    private final SongService songService;

    private final UserService userService;

    public PlayerController(PlaylistService playlistService, SongService songService, UserService userService) {
        this.playlistService = playlistService;
        this.songService = songService;
        this.userService = userService;
        this.resourceURIBuilder = new ResourceURIBuilder(PlayerController.class);
    }

    @GetMapping("/player_old")
    public String showOldPlayer(Model model) {
        model.addAttribute("songs", songService.getAllForCurrentUser().stream()
                .collect(Collectors.toMap(song -> createSongsURI(Paths.get(song.getUri())), Song::getDisplayName)));
        // TODO fix this hack, use the same in `player` to be able to play playlist
        model.addAttribute("songsUri", "/user/songs");
        return "app/player/player_old.html";
    }

    @GetMapping("/player")
    public String showPlayer(@RequestParam(value = "playlist", required = false, defaultValue = "-1") long playlist,
                             Model model) {
        if (playlist >= 0) {
            model.addAttribute("songsUri", String.format("/user/songs/playlist/%s", playlist));
        } else {
            model.addAttribute("songsUri", "/user/songs");
        }
        return "app/player/player.html";
    }

    @GetMapping("/play/{songName}")
    public String playSong(@PathVariable String songName, Model model) {
        model.addAttribute("file", resourceURIBuilder.withClearState()
                .withMethodName("serveFile")
                .withParameters(songName).build());
        return "app/player/player_old.html";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws StoreException {
        Resource file = songService.serve(new String(Base64.getDecoder().decode(filename), UTF_8));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                String.format(HEADER_VALUE_ATTACHMENT, file.getFilename()))
                .body(file);
    }

    @GetMapping(value = "/songs", produces = "application/json")
    public ResponseEntity<List<SongDto>> getAllUserSongs() {
        return ResponseEntity.ok(songService.getAllForCurrentUser()
                .stream().map(this::convertToSongDto)
                .collect(Collectors.toList()));
    }

    @GetMapping(value = "/songs/count", produces = "application/json")
    public ResponseEntity<Map<String, ?>> getUserSongCount() {
        return ResponseEntity.ok(Map.of("numberOfSongs", songService.getSongsCount(userService.getCurrentUser())));
    }

    @GetMapping(value = "/songs/playlist/{playlistId}", produces = "application/json")
    public ResponseEntity<List<SongDto>> getSongsForPlaylist(@PathVariable long playlistId) throws PlaylistNotExistException {
        return ResponseEntity.ok(playlistService.getSongs(playlistId)
                .stream()
                .map(this::convertToSongDto)
                .collect(Collectors.toList()));
    }

    private String createSongsURI(Path path) {
        return resourceURIBuilder.withClearState()
                .withMethodName("playSong")
                .withPath(path)
                .withParameters(Map.of())
                .build();
    }

    private SongDto convertToSongDto(Song song) {
        return new SongDto(song.getDisplayName(),
                resourceURIBuilder
                        .withClearState()
                        .withMethodName("serveFile")
                        .withPath(Base64.getEncoder().encodeToString(song.getUri().getBytes(UTF_8)))
                        .build());
    }

    public static class SongDto {

        private final String title;

        private final String file;

        private final String howl;

        private SongDto(String title, String file) {
            this.title = title;
            this.file = file;
            howl = null;
        }

        public String getTitle() {
            return title;
        }

        public String getFile() {
            return file;
        }

        public String getHowl() {
            return howl;
        }
    }
}
