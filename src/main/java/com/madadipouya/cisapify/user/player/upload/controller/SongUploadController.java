package com.madadipouya.cisapify.user.player.upload.controller;

import com.madadipouya.cisapify.user.player.upload.service.UploadService;
import com.madadipouya.cisapify.user.player.upload.service.exception.StorageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SongUploadController {

    private final UploadService uploadService;

    public SongUploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/upload")
    public String postUploadSong(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws StorageException {

        uploadService.store(file);

        redirectAttributes.addFlashAttribute("message",
            "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/upload.html";
    }

    @GetMapping("/upload.html")
    public String showVetList() {
        return "user/player/upload/song_upload";
    }
}
