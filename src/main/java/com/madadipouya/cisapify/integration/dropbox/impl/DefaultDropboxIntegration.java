package com.madadipouya.cisapify.integration.dropbox.impl;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.json.JsonReadException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.i18n.service.I18nService;
import com.madadipouya.cisapify.integration.base.exception.FailRetrievingRemoteObjectException;
import com.madadipouya.cisapify.integration.base.exception.SongsListRetrievalException;
import com.madadipouya.cisapify.integration.dropbox.DropboxIntegration;
import com.madadipouya.cisapify.integration.dropbox.DropboxProperties;
import com.madadipouya.cisapify.integration.dropbox.exception.DropboxIntegrationException;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.service.UserService;
import com.madadipouya.cisapify.util.ApplicationContextUtil;
import com.madadipouya.cisapify.util.SongUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultDropboxIntegration implements DropboxIntegration {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDropboxIntegration.class);

    private static final String REDIRECT_URL = "%s/user/dropbox/authentication";

    private static final String ROOT_PATH = StringUtils.EMPTY;

    private static final String TEMP_FULL_PATH = "/tmp/%s.mp3";

    private final DropboxProperties dropboxProperties;

    private final ApplicationContextUtil applicationContextUtil;

    private final UserService userService;

    private final I18nService i18nService;

    public DefaultDropboxIntegration(DropboxProperties dropboxProperties, ApplicationContextUtil applicationContextUtil,
                                     UserService userService, I18nService i18nService) {
        this.dropboxProperties = dropboxProperties;
        this.applicationContextUtil = applicationContextUtil;
        this.userService = userService;
        this.i18nService = i18nService;
    }

    @Override
    public String getAuthorizationUrl(HttpServletRequest request) throws DropboxIntegrationException {
        try {
            logger.info(applicationContextUtil.getBaseUrl());
            DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder()
                    .withRedirectUri(String.format(REDIRECT_URL, applicationContextUtil.getBaseUrl()), getSessionStore(request)).build();
            return getWebAuth().authorize(authRequest);
        } catch (JsonReadException configLoadException) {
            throw new DropboxIntegrationException(i18nService.getMessage("dropbox.integration.failedToLoadConfig"), configLoadException);
        }
    }

    @Override
    public User finishAuthentication(HttpServletRequest request) throws DropboxIntegrationException {
        try {
            DbxAuthFinish authFinish = getWebAuth().finishFromRedirect(String.format(REDIRECT_URL, applicationContextUtil.getBaseUrl()),
                    getSessionStore(request), request.getParameterMap());
            return updateUserProfile(authFinish.getAccessToken());

        } catch (JsonReadException configLoadException) {
            throw new DropboxIntegrationException(i18nService.getMessage("dropbox.integration.failedToLoadConfig"), configLoadException);
        } catch (DbxWebAuth.BadRequestException | DbxWebAuth.BadStateException | DbxWebAuth.CsrfException
                | DbxWebAuth.NotApprovedException | DbxWebAuth.ProviderException | DbxException dropboxAuthorizationException) {
            throw new DropboxIntegrationException(i18nService.getMessage("dropbox.integration.noneSuccessResponse"), dropboxAuthorizationException);
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
            throw new SongsListRetrievalException(i18nService.getMessage("dropbox.integration.failedToLoadSongsList"), dropboxException);
        }
    }

    @Override
    public Path loadRemoteSong(String token, Song song) throws FailRetrievingRemoteObjectException, IOException {
        try {
            DbxClientV2 client = new DbxClientV2(getRequestConfig(), token);
            Path storedPath = Path.of(String.format(TEMP_FULL_PATH, song.getFileName()));
            if (!Files.exists(storedPath)) {
                Files.copy(client.files().download(song.getUri()).getInputStream(), storedPath);
            }
            return storedPath;
        } catch (DbxException dropboxException) {
            throw new FailRetrievingRemoteObjectException(
                    i18nService.getMessage("dropbox.integration.failedToLoadSongBlog", song.getId()), dropboxException);
        }
    }

    private DbxAppInfo getAppInfo() throws JsonReadException {
        return DbxAppInfo.Reader.readFully(dropboxProperties.getAppInfoAsJson());
    }

    private DbxWebAuth getWebAuth() throws JsonReadException {
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