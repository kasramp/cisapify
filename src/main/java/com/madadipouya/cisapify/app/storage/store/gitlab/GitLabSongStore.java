package com.madadipouya.cisapify.app.storage.store.gitlab;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.storage.StorageType;
import com.madadipouya.cisapify.app.storage.store.AbstractSongStore;
import com.madadipouya.cisapify.app.storage.store.SongStore;
import com.madadipouya.cisapify.app.storage.store.StoredFileDetails;
import com.madadipouya.cisapify.app.storage.store.exception.StoreException;
import com.madadipouya.cisapify.app.storage.store.exception.StoreOperationNotSupportedException;
import com.madadipouya.cisapify.app.storage.store.exception.StoreRemoteObjectRetrievingException;
import com.madadipouya.cisapify.i18n.service.I18nService;
import com.madadipouya.cisapify.integration.base.exception.FailRetrievingRemoteObjectException;
import com.madadipouya.cisapify.integration.gitlab.GitLabIntegration;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class GitLabSongStore extends AbstractSongStore implements SongStore {

    private final GitLabIntegration gitLabIntegration;

    private final I18nService i18nService;

    public GitLabSongStore(GitLabIntegration gitLabIntegration, I18nService i18nService) {
        this.gitLabIntegration = gitLabIntegration;
        this.i18nService = i18nService;
    }

    @Override
    public Resource load(Song song) throws StoreRemoteObjectRetrievingException {
        try {
            return super.load(gitLabIntegration.loadRemoteSong(song.getUser().getGitlabToken(), song));
        } catch (IOException | FailRetrievingRemoteObjectException | StoreException exception) {
            throw new StoreRemoteObjectRetrievingException(i18nService.getMessage("gitlab.store.failedToLoadSong", song.getId()), exception);
        }
    }

    @Override
    public StoredFileDetails store(MultipartFile file) {
        throw new StoreOperationNotSupportedException(i18nService.getMessage("gitlab.store.noSaveSupport"));
    }

    @Override
    public void delete(Song song) {
        throw new StoreOperationNotSupportedException(i18nService.getMessage("gitlab.store.noDeleteSupport"));
    }

    @Override
    public StorageType getSupportedType() {
        return StorageType.REMOTE_GIT_LAB;
    }
}
