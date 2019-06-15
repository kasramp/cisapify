package com.madadipouya.cisapify.integration.dropbox.controller;

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

    public DropboxAuthenticationController(DropboxIntegration dropboxIntegration, DropboxSongIndexer dropboxSongIndexer) {
        this.dropboxIntegration = dropboxIntegration;
        this.dropboxSongIndexer = dropboxSongIndexer;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> authenticate(HttpServletRequest request) {
        try {
            return ResponseEntity.ok().body(Map.of("redirect", dropboxIntegration.getAuthorizationUrl(request)));
        } catch (DropboxIntegrationException dropboxIntegrationException) {
            logger.warn("Failed to get authorization url for Dropbox", dropboxIntegrationException);
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
            logger.warn("Failed to get finish authorization to Dropbox", dropboxIntegrationException);
        }
        response.sendRedirect("/user/profile?error");
    }
}
