package com.madadipouya.cisapify.app.upload.controller;

import com.madadipouya.cisapify.app.upload.service.UploadService;
import com.madadipouya.cisapify.app.upload.service.exception.StorageException;
import com.madadipouya.cisapify.i18n.service.I18nService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SongUploadController {

    private final UploadService uploadService;

    private final I18nService i18nService;

    public SongUploadController(UploadService uploadService, I18nService i18nService) {
        this.uploadService = uploadService;
        this.i18nService = i18nService;
    }

    @PostMapping("/upload")
    public String doUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Authentication authentication) throws StorageException {
        uploadService.store(file, authentication.getName());
        redirectAttributes.addFlashAttribute("message", i18nService.getMessage("upload.controller.successFileUpload", file.getOriginalFilename()));
        return "redirect:/upload";
    }

    @GetMapping("/upload")
    public String showUploadForm() {
        return "app/player/upload/song_upload";
    }
}
