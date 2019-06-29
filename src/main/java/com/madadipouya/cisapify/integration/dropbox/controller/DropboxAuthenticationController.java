package com.madadipouya.cisapify.integration.dropbox.controller;

import com.madadipouya.cisapify.i18n.service.I18nService;
import com.madadipouya.cisapify.integration.dropbox.DropboxIntegration;
import com.madadipouya.cisapify.integration.dropbox.DropboxSongIndexer;
import com.madadipouya.cisapify.integration.dropbox.exception.DropboxIntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/user/dropbox/authentication")
public class DropboxAuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(DropboxAuthenticationController.class);

    private final DropboxIntegration dropboxIntegration;

    private final DropboxSongIndexer dropboxSongIndexer;

    private final I18nService i18nService;

    public DropboxAuthenticationController(DropboxIntegration dropboxIntegration, DropboxSongIndexer dropboxSongIndexer, I18nService i18nService) {
        this.dropboxIntegration = dropboxIntegration;
        this.dropboxSongIndexer = dropboxSongIndexer;
        this.i18nService = i18nService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> authenticate(HttpServletRequest request) {
        try {
            return ResponseEntity.ok().body(Map.of("redirect", dropboxIntegration.getAuthorizationUrl(request)));
        } catch (DropboxIntegrationException dropboxIntegrationException) {
            logger.warn(i18nService.getMessage("dropbox.controller.authentication.failedToGetAuthUrl"), dropboxIntegrationException);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @GetMapping
    public void handleCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // TODO add more message to success or failure
        try {
            dropboxSongIndexer.reindexDropboxSongsAsync(dropboxIntegration.finishAuthentication(request));
            response.sendRedirect("/user/profile?success");
            return;
        } catch (DropboxIntegrationException dropboxIntegrationException) {
            logger.warn(i18nService.getMessage("dropbox.controller.authentication.failedToCompleteAuth"), dropboxIntegrationException);
        }
        response.sendRedirect("/user/profile?error");
    }
}
