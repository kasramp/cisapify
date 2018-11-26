package com.madadipouya.cisapify.user.player.upload.controller;

import com.madadipouya.cisapify.user.player.upload.service.UploadService;
import com.madadipouya.cisapify.user.player.upload.service.exception.StorageException;
import com.madadipouya.cisapify.user.player.upload.service.exception.StorageFileNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PlayerController {

    private final UploadService uploadService;

    public PlayerController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @GetMapping("/player_old")
    public String showPlayer(Map<String, Object> model) throws StorageException {
        model.put("songs", uploadService.loadAll().collect(Collectors.toMap(this::createResourceURI, Path::getFileName)));
        return "user/player/player_old.html";
    }

    @GetMapping("/player")
    public String showPlayer1(Map<String, Object> model) throws StorageException {
        model.put("songs", uploadService.loadAll().collect(Collectors.toMap(this::createResourceURI, Path::getFileName)));
        return "user/player/player.html";
    }

    @GetMapping("/play/{songName}")
    public String playSong(@PathVariable String songName, Map<String, Object> model) {
        model.put("file", MvcUriComponentsBuilder.fromMethodName(PlayerController.class, "serveFile", songName).build().toString());
        return "user/player/player_old.html";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws StorageFileNotFoundException {
        Resource file = uploadService.load(URLDecoder.decode(filename, StandardCharsets.UTF_8));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping(value = "/playlist", produces = "application/json")
    public ResponseEntity<List<SongDto>> getPlayList(HttpServletRequest request) throws StorageException {
        return ResponseEntity.ok(uploadService.loadAll()
                .map(path -> new SongDto(path.getFileName().toString(), getServiceResourceURI(path))).collect(Collectors.toList()));
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

    private String createResourceURI(Path path) {
        return MvcUriComponentsBuilder.fromMethodName(PlayerController.class,
                "playSong", URLEncoder.encode(path.getFileName().toString(), StandardCharsets.UTF_8), new HashMap<>()).build().toString();
    }

    private String getServiceResourceURI(Path path) {
        return MvcUriComponentsBuilder.fromMethodName(PlayerController.class,
                "serveFile", URLEncoder.encode(path.getFileName().toString(), StandardCharsets.UTF_8)).build().toString();
    }
}
