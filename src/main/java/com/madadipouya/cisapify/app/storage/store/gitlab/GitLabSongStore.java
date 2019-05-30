package com.madadipouya.cisapify.app.storage.store.gitlab;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.storage.StorageType;
import com.madadipouya.cisapify.app.storage.store.AbstractSongStore;
import com.madadipouya.cisapify.app.storage.store.SongStore;
import com.madadipouya.cisapify.app.storage.store.exception.StoreException;
import com.madadipouya.cisapify.app.storage.store.exception.StoreOperationNotSupportedException;
import com.madadipouya.cisapify.app.storage.store.exception.StoreRemoteObjectRetrievingException;
import com.madadipouya.cisapify.integration.gitlab.GitLabIntegration;
import com.madadipouya.cisapify.integration.gitlab.exception.FailRetrievingRemoteObjectException;
import com.madadipouya.cisapify.user.model.User;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class GitLabSongStore extends AbstractSongStore implements SongStore {

    private final GitLabIntegration gitLabIntegration;

    public GitLabSongStore(GitLabIntegration gitLabIntegration) {
        this.gitLabIntegration = gitLabIntegration;
    }

    @Override
    public Resource load(Song song) throws StoreRemoteObjectRetrievingException {
        try {
            return super.load(gitLabIntegration.loadRemoteSong(song.getUser().getGitlabToken(), song));
        } catch (IOException | FailRetrievingRemoteObjectException | StoreException exception) {
            throw new StoreRemoteObjectRetrievingException(String.format("Failed to load the song id: %s", song.getId()), exception);
        }
    }

    @Override
    public String store(MultipartFile file, User user) {
        throw new StoreOperationNotSupportedException("GitLab store does not support store/save mode.");
    }

    @Override
    public void delete(Song song) {
        throw new StoreOperationNotSupportedException("GitLab does not support delete mode.");
    }

    @Override
    public StorageType getSupportedType() {
        return StorageType.REMOTE_GIT_LAB;
    }
}
