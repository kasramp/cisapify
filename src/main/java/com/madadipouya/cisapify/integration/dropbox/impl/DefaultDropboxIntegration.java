package com.madadipouya.cisapify.integration.dropbox.impl;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.integration.base.exception.SongsListRetrievalException;
import com.madadipouya.cisapify.integration.dropbox.DropboxIntegration;
import com.madadipouya.cisapify.integration.dropbox.exception.DropboxIntegrationException;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.service.UserService;
import com.madadipouya.cisapify.util.ApplicationContextUtil;
import com.madadipouya.cisapify.util.SongUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultDropboxIntegration implements DropboxIntegration {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDropboxIntegration.class);

    private static final String REDIRECT_URL = "%s/user/dropbox/authentication";

    private static final String ROOT_PATH = StringUtils.EMPTY;

    // TODO cache reading the file content
    @Value("classpath:dropboxAppInfo.json")
    private Resource dropboxAppInfo;

    private final ApplicationContextUtil applicationContextUtil;

    private final UserService userService;

    public DefaultDropboxIntegration(ApplicationContextUtil applicationContextUtil, UserService userService) {
        this.applicationContextUtil = applicationContextUtil;
        this.userService = userService;
    }

    @Override
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

    @Override
    public User finishAuthentication(HttpServletRequest request) throws DropboxIntegrationException {
        try {
            DbxAuthFinish authFinish = getWebAuth().finishFromRedirect(String.format(REDIRECT_URL, applicationContextUtil.getBaseUrl()),
                    getSessionStore(request), request.getParameterMap());

            logger.info("The token is {}", authFinish.getAccessToken());
            return updateUserProfile(authFinish.getAccessToken());

        } catch (IOException | JsonReader.FileLoadException configLoadException) {
            throw new DropboxIntegrationException("Failed to load Dropbox configuration file", configLoadException);
        } catch (DbxWebAuth.BadRequestException | DbxWebAuth.BadStateException | DbxWebAuth.CsrfException
                | DbxWebAuth.NotApprovedException | DbxWebAuth.ProviderException | DbxException dropboxAuthorizationException) {
            throw new DropboxIntegrationException("Received none 200 response from Dropbox", dropboxAuthorizationException);
        }
    }

    @Override
    public List<Song> getSongs(User user) throws SongsListRetrievalException {
        try {
            return new DbxClientV2(getRequestConfig(), user.getDropboxToken())
                    .files()
                    .listFolderBuilder(ROOT_PATH)
                    .withRecursive(true)
                    .start()
                    .getEntries()
                    .stream()
                    .filter(this::isAudioFile)
                    .map(file -> transformToSong((FileMetadata) file, user))
                    .collect(Collectors.toList());
        } catch (DbxException dropboxException) {
            throw new SongsListRetrievalException("Fail to retrieve songs list", dropboxException);
        }
    }

    private DbxAppInfo getAppInfo() throws IOException, JsonReader.FileLoadException {
        return DbxAppInfo.Reader.readFromFile(dropboxAppInfo.getURI().getPath());
    }

    private DbxWebAuth getWebAuth() throws IOException, JsonReader.FileLoadException {
        return new DbxWebAuth(getRequestConfig(), getAppInfo());
    }

    private DbxRequestConfig getRequestConfig() {
        return new DbxRequestConfig(applicationContextUtil.getApplicationName());
    }

    private DbxSessionStore getSessionStore(final HttpServletRequest request) {
        return new DbxStandardSessionStore(request.getSession(true), "dropbox-auth-csrf-token");
    }

    private User updateUserProfile(String accessToken) {
        User user = userService.getCurrentUser();
        user.setDropboxToken(accessToken);
        return userService.save(user);
    }

    private boolean isAudioFile(Metadata file) {
        return file instanceof FileMetadata && SongUtil.isAudioFile(file.getName());
    }

    private Song transformToSong(FileMetadata file, User user) {
        return Song.Builder()
                .withUser(user)
                .withDisplayName(file.getName())
                .withFileName(file.getContentHash())
                .withUri(file.getPathLower())
                .withDropboxSource()
                .build();
    }
}