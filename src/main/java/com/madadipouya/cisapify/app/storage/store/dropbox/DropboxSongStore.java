package com.madadipouya.cisapify.app.storage.store.dropbox;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.storage.StorageType;
import com.madadipouya.cisapify.app.storage.store.AbstractSongStore;
import com.madadipouya.cisapify.app.storage.store.SongStore;
import com.madadipouya.cisapify.app.storage.store.StoredFileDetails;
import com.madadipouya.cisapify.app.storage.store.exception.StoreException;
import com.madadipouya.cisapify.app.storage.store.exception.StoreOperationNotSupportedException;
import com.madadipouya.cisapify.app.storage.store.exception.StoreRemoteObjectRetrievingException;
import com.madadipouya.cisapify.integration.base.exception.FailRetrievingRemoteObjectException;
import com.madadipouya.cisapify.integration.dropbox.DropboxIntegration;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class DropboxSongStore extends AbstractSongStore implements SongStore {

    private final DropboxIntegration dropboxIntegration;

    public DropboxSongStore(DropboxIntegration dropboxIntegration) {
        this.dropboxIntegration = dropboxIntegration;
    }

    @Override
    public Resource load(Song song) throws StoreRemoteObjectRetrievingException {
        try {
            return super.load(dropboxIntegration.loadRemoteSong(song.getUser().getDropboxToken(), song));
        } catch (IOException | FailRetrievingRemoteObjectException | StoreException exception) {
            throw new StoreRemoteObjectRetrievingException(String.format("Failed to load the song id: %s of Dropbox", song.getId()), exception);
        }
    }

    @Override
    public StoredFileDetails store(MultipartFile file) {
        throw new StoreOperationNotSupportedException("Dropbox store does not support store/save mode at this moment.");
    }

    @Override
    public void delete(Song song) {
        throw new StoreOperationNotSupportedException("Dropbox does not support delete mode at this moment.");
    }

    @Override
    public StorageType getSupportedType() {
        return StorageType.REMOTE_DROPBOX;
    }
}
