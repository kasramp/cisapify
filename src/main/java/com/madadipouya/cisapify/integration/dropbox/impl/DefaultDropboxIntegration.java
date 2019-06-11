package com.madadipouya.cisapify.integration.dropbox.impl;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.json.JsonReader;
import com.madadipouya.cisapify.integration.dropbox.exception.DropboxIntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Service
public class DefaultDropboxIntegration {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDropboxIntegration.class);

    // TODO change URL
    private static final String REDIRECT_URL = "http://localhost:8090/user/dropbox/authentication";


    // TODO cache reading the file content
    @Value("classpath:dropboxAppInfo.json")
    private Resource dropboxAppInfo;


    public String getAuthorizationUrl(HttpServletRequest request) throws DropboxIntegrationException {
        try {
            DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder().withRedirectUri(REDIRECT_URL, getSessionStore(request)).build();
            return getWebAuth().authorize(authRequest);
        } catch (IOException | JsonReader.FileLoadException configLoadException) {
            throw new DropboxIntegrationException("Failed to load Dropbox configuration file", configLoadException);
        }
    }

    public void finishAuthentication(HttpServletRequest request) throws DropboxIntegrationException {
        try {
            DbxAuthFinish authFinish = getWebAuth().finishFromRedirect(REDIRECT_URL, getSessionStore(request), request.getParameterMap());
            logger.info("The token is {}", authFinish.getAccessToken());
        } catch (IOException | JsonReader.FileLoadException configLoadException) {
            throw new DropboxIntegrationException("Failed to load Dropbox configuration file", configLoadException);
        } catch (DbxWebAuth.BadRequestException | DbxWebAuth.BadStateException | DbxWebAuth.CsrfException
                | DbxWebAuth.NotApprovedException | DbxWebAuth.ProviderException | DbxException dropboxAuthorizationException) {
            throw new DropboxIntegrationException("Received none 200 response from Dropbox", dropboxAuthorizationException);
        }
    }


    private DbxAppInfo getAppInfo() throws IOException, JsonReader.FileLoadException {
        return DbxAppInfo.Reader.readFromFile(dropboxAppInfo.getURI().getPath());
    }

    private DbxWebAuth getWebAuth() throws IOException, JsonReader.FileLoadException {
        return new DbxWebAuth(new DbxRequestConfig("examples-authorize"), getAppInfo());
    }

    private DbxSessionStore getSessionStore(final HttpServletRequest request) {
        // Select a spot in the session for DbxWebAuth to store the CSRF token.
        HttpSession session = request.getSession(true);
        String sessionKey = "dropbox-auth-csrf-token";
        return new DbxStandardSessionStore(session, sessionKey);
    }
}