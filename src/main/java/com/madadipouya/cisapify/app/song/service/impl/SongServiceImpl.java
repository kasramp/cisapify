package com.madadipouya.cisapify.app.song.service.impl;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.song.repository.SongRepository;
import com.madadipouya.cisapify.app.song.service.SongService;
import com.madadipouya.cisapify.app.storage.service.SongStorageService;
import com.madadipouya.cisapify.app.storage.store.StoredFileDetails;
import com.madadipouya.cisapify.app.storage.store.exception.StoreException;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

@Service
@CacheConfig(cacheNames = {"songsCount"})
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    private final UserService userService;

    private final SongStorageService storageService;

    public SongServiceImpl(SongRepository songRepository, UserService userService, SongStorageService storageService) {
        this.songRepository = songRepository;
        this.userService = userService;
        this.storageService = storageService;
    }

    @Override
    public String getDisplayName(Path path) {
        return songRepository.findByFileName(path.getFileName().toString()).getDisplayName();
    }

    @Override
    public Song save(Song song) {
        return songRepository.save(song);
    }

    //TODO do proper exception handling
    public Song findById(long songId) {
        return songRepository.findById(songId).get();
    }

    @Override
    public Song findByUri(String uri) {
        return songRepository.findByUri(uri);
    }

    @Override
    public List<Song> getAllForCurrentUser() {
        return songRepository.findByUserOrderByDisplayName(userService.getCurrentUser());
    }

    @Override
    public List<Song> getAllByUserId(long userId) {
        return songRepository.findByUserId(userId);
    }

    @Override
    @CacheEvict(key = "{ #songs.size() > 0 ? #songs[0].user.id : -1 }")
    public void deleteAll(List<Song> songs) {
        songRepository.deleteAll(songs);
    }

    @Override
    @CacheEvict(key = "{ #songs.size() > 0 ? #songs[0].user.id : -1 }")
    public void saveAll(List<Song> songs) {
        songRepository.saveAll(songs);
    }

    @Override
    public Resource serve(String songUri) throws StoreException {
        return storageService.load(findByUri(songUri));
    }

    @Override
    @CacheEvict(key = "{ #user.id }")
    public String save(User user, MultipartFile file) throws StoreException {
        StoredFileDetails storedFileDetails = storageService.store(file);
        songRepository.save(Song.Builder().withDisplayName(storedFileDetails.getDisplayName())
                .withFileName(storedFileDetails.getFileName())
                .withUri(storedFileDetails.getUri())
                .withUser(user)
                .build());
        return storedFileDetails.getDisplayName();
    }

    @Override
    @Cacheable(key = "{ #user.id }")
    public long getSongsCount(User user) {
        return songRepository.countByUser(user);
    }
}