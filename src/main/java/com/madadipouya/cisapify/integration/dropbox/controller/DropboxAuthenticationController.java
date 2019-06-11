package com.madadipouya.cisapify.integration.dropbox.controller;


import com.madadipouya.cisapify.integration.dropbox.exception.DropboxIntegrationException;
import com.madadipouya.cisapify.integration.dropbox.impl.DefaultDropboxIntegration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    // TODO remove autowire
    @Autowired
    private DefaultDropboxIntegration dropboxIntegration;

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
            dropboxIntegration.finishAuthentication(request);
            response.sendRedirect(request.getContextPath() + "/user/profile?success");
        } catch (DropboxIntegrationException dropboxIntegrationException) {
            logger.warn("Failed to get finish authorization to Dropbox", dropboxIntegrationException);
        }
        response.sendRedirect(request.getContextPath() + "/user/profile?error");
    }
}
