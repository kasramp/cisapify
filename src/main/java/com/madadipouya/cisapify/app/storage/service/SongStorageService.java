package com.madadipouya.cisapify.app.storage.service;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.storage.store.exception.StoreException;
import com.madadipouya.cisapify.user.model.User;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface SongStorageService {

    Resource load(Song song) throws StoreException;

    void store(MultipartFile file, User user) throws StoreException;

    void delete(Song song);
}