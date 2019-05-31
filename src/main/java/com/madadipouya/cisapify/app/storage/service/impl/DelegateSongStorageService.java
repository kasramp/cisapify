package com.madadipouya.cisapify.app.storage.service.impl;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.storage.service.SongStorageService;
import com.madadipouya.cisapify.app.storage.store.SongStoreFactory;
import com.madadipouya.cisapify.app.storage.store.StoredFileDetails;
import com.madadipouya.cisapify.app.storage.store.exception.StoreException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DelegateSongStorageService implements SongStorageService {

    private final SongStoreFactory songStoreFactory;

    public DelegateSongStorageService(SongStoreFactory songStoreFactory) {
        this.songStoreFactory = songStoreFactory;
    }

    @Override
    public Resource load(Song song) throws StoreException {
        return songStoreFactory.getStore(song).load(song);
    }

    @Override
    public StoredFileDetails store(MultipartFile file) throws StoreException {
        return songStoreFactory.getStore().store(file);
    }

    @Override
    public void delete(Song song) {
        songStoreFactory.getStore(song).delete(song);
    }
}
