package com.madadipouya.cisapify.app.player.controller;

import com.madadipouya.cisapify.app.song.service.SongService;
import com.madadipouya.cisapify.app.upload.service.UploadService;
import com.madadipouya.cisapify.app.upload.service.exception.StorageException;
import com.madadipouya.cisapify.app.upload.service.exception.StorageFileNotFoundException;
import com.madadipouya.cisapify.util.ResourceURIBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Controller
public class PlayerController {

    private final UploadService uploadService;

    private final ResourceURIBuilder resourceURIBuilder;

    private final SongService songService;

    public PlayerController(UploadService uploadService, SongService songService) {
        this.uploadService = uploadService;
        this.songService = songService;
        this.resourceURIBuilder = new ResourceURIBuilder(PlayerController.class);
    }

    @GetMapping("/player_old")
    public String showOldPlayer(Map<String, Object> model) throws StorageException {
        model.put("songs", uploadService.loadAll().collect(Collectors.toMap(this::createSongsURI, songService::getDisplayName)));
        return "app/player/player_old.html";
    }

    @GetMapping("/player")
    public String showPlayer() {
        return "app/player/player.html";
    }

    @GetMapping("/play/{songName}")
    public String playSong(@PathVariable String songName, Map<String, Object> model) {
        model.put("file", resourceURIBuilder.withClearState().withMethodName("serveFile").withParameters(songName).build());
        return "app/player/player_old.html";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws StorageFileNotFoundException {
        Resource file = uploadService.load(URLDecoder.decode(filename, UTF_8));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping(value = "/playlist", produces = "application/json")
    public ResponseEntity<List<SongDto>> getPlayList() throws StorageException {
        return ResponseEntity.ok(uploadService.loadAll()
                .map(path -> new SongDto(songService.getDisplayName(path),
                        resourceURIBuilder.withClearState().withMethodName("serveFile").withPath(path).build())
                ).collect(Collectors.toList()));
    }

    @GetMapping(value = "/playlist_new", produces = "application/json")
    public ResponseEntity<List<SongDto>> getPlayListNew() throws StorageException {
        return ResponseEntity.ok(uploadService.loadAllForCurrentUser()
                .map(path -> new SongDto(songService.getDisplayName(path),
                        resourceURIBuilder.withClearState().withMethodName("serveFile").withPath(path).build())
                ).collect(Collectors.toList()));
    }

    private String createSongsURI(Path path) {
        return resourceURIBuilder.withClearState()
                .withMethodName("playSong")
                .withPath(path)
                .withParameters(Map.of())
                .build();
    }

    public static class SongDto {

        private String title;

        private String file;

        private String howl = null;

        public SongDto(String title, String file) {
            this.title = title;
            this.file = file;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getHowl() {
            return howl;
        }
    }
}