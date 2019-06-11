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
import com.madadipouya.cisapify.util.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public class DefaultDropboxIntegration {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDropboxIntegration.class);

    private static final String REDIRECT_URL = "%s/user/dropbox/authentication";

    // TODO cache reading the file content
    @Value("classpath:dropboxAppInfo.json")
    private Resource dropboxAppInfo;

    private final ApplicationContextUtil applicationContextUtil;

    public DefaultDropboxIntegration(ApplicationContextUtil applicationContextUtil) {
        this.applicationContextUtil = applicationContextUtil;
    }

    public String getAuthorizationUrl(HttpServletRequest request) throws DropboxIntegrationException {
        try {
            logger.info(applicationContextUtil.getBaseUrl());
            DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder()
                    .withRedirectUri(String.format(REDIRECT_URL, applicationContextUtil.getBaseUrl()), getSessionStore(request)).build();
            return getWebAuth().authorize(authRequest);
        } catch (IOException | JsonReader.FileLoadException configLoadException) {
            throw new DropboxIntegrationException("Failed to load Dropbox configuration file", configLoadException);
        }
    }

    public void finishAuthentication(HttpServletRequest request) throws DropboxIntegrationException {
        try {
            DbxAuthFinish authFinish = getWebAuth().finishFromRedirect(String.format(REDIRECT_URL, applicationContextUtil.getBaseUrl()),
                    getSessionStore(request), request.getParameterMap());
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
        return new DbxWebAuth(new DbxRequestConfig(applicationContextUtil.getApplicationName()), getAppInfo());
    }

    private DbxSessionStore getSessionStore(final HttpServletRequest request) {
        return new DbxStandardSessionStore(request.getSession(true), "dropbox-auth-csrf-token");
    }
}