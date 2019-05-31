package com.madadipouya.cisapify.app.song.service;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.storage.store.exception.StoreException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface SongService {

    /**
     * Retrieves {@link Song#displayName} from database based on the file name in path
     *
     * @param path contains the file name
     * @return song display name
     */
    String getDisplayName(Path path);

    /**
     * Persists a {@link Song} to the database
     *
     * @param song to persist
     * @return the persisted instance of {@link Song} with {@link Song#id}
     */
    Song save(Song song);

    /**
     * Retrieves a list of {@link Song} based on the loggedIn {@link com.madadipouya.cisapify.user.model.User}
     *
     * @return list of Songs
     */
    List<Song> getAllForCurrentUser();

    List<Song> getAllByUserId(long userId);

    /**
     * Retrieves a {@link Song} by {@link Song#id}
     *
     * @param songId to lookup
     * @return song associated with the given id
     */
    Song findById(long songId);

    Song findByUri(String uri);

    void deleteAll(List<Song> songs);

    void saveAll(List<Song> songs);

    Resource serve(String songUri) throws StoreException;

    String save(MultipartFile file) throws StoreException;
}
