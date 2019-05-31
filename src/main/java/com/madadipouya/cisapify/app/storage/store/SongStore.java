package com.madadipouya.cisapify.app.storage.store;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.storage.StorageType;
import com.madadipouya.cisapify.app.storage.store.exception.StoreException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface SongStore extends Store {

    Resource load(Song song) throws StoreException;

    StoredFileDetails store(MultipartFile file) throws StoreException;

    void delete(Song song);

    StorageType getSupportedType();
}
