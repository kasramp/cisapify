package com.madadipouya.cisapify.app.upload.controller;

import com.madadipouya.cisapify.app.song.service.SongService;
import com.madadipouya.cisapify.app.storage.store.exception.StoreException;
import com.madadipouya.cisapify.i18n.service.I18nService;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.desktop.UserSessionEvent;

@Controller
@RequestMapping("/user")
public class SongUploadController {

    private final SongService songService;

    private final UserService userService;

    private final I18nService i18nService;

    public SongUploadController(SongService songService, UserService userService, I18nService i18nService) {
        this.songService = songService;
        this.userService = userService;
        this.i18nService = i18nService;
    }

    @PostMapping("/upload")
    public String doUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws StoreException {
        redirectAttributes.addFlashAttribute("message",
                i18nService.getMessage("upload.controller.successFileUpload", songService.save(userService.getCurrentUser(), file)));
        return "redirect:/user/upload";
    }

    @GetMapping("/upload")
    public String showUploadForm() {
        return "app/upload/song_upload";
    }
}
