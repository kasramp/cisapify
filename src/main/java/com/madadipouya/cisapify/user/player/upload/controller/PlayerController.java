package com.madadipouya.cisapify.user.player.upload.controller;

import com.madadipouya.cisapify.user.player.upload.service.UploadService;
import com.madadipouya.cisapify.user.player.upload.service.exception.StorageException;
import com.madadipouya.cisapify.user.player.upload.service.exception.StorageFileNotFoundException;
import com.madadipouya.cisapify.util.ResourceURIBuilder;
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

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodName;

@Controller
public class PlayerController {

    private final UploadService uploadService;

    private final ResourceURIBuilder resourceURIBuilder;

    public PlayerController(UploadService uploadService) {
        this.uploadService = uploadService;
        this.resourceURIBuilder = new ResourceURIBuilder(PlayerController.class);
    }

    @GetMapping("/player_old")
    public String showOldPlayer(Map<String, Object> model) throws StorageException {
        model.put("songs", uploadService.loadAll().collect(Collectors.toMap(this::createResourceURI, Path::getFileName)));
        return "user/player/player_old.html";
    }

    @GetMapping("/player")
    public String showPlayer(Map<String, Object> model) throws StorageException {
        model.put("songs", uploadService.loadAll().collect(Collectors.toMap(this::createResourceURI, Path::getFileName)));
        return "user/player/player.html";
    }

    @GetMapping("/play/{songName}")
    public String playSong(@PathVariable String songName, Map<String, Object> model) {
        model.put("file", fromMethodName(PlayerController.class, "serveFile", songName).build().toString());
        return "user/player/player_old.html";
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

    private static String createResourceURI(Class<?> clazz, String methodName, Path path, Object... args) {
        return fromMethodName(clazz, methodName, encode(path.getFileName().toString(), UTF_8), args)
                .build()
                .toString();
    }

    private String createResourceURI(Path path) {
        return fromMethodName(PlayerController.class,
                "playSong", encode(path.getFileName().toString(), UTF_8), new HashMap<>()).build().toString();
    }

    private String getServiceResourceURI(Path path) {
        return fromMethodName(PlayerController.class,
                "serveFile", encode(path.getFileName().toString(), UTF_8)).build().toString();
    }
}
