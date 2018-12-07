package com.madadipouya.cisapify.app.song.service;

import java.nio.file.Path;
import java.util.List;

import com.madadipouya.cisapify.app.song.model.Song;

public interface SongService {

    /**
     * Retrieves {@link Song#displayName} from database based on the file name in path
     * @param path contains the file name
     * @return song display name
     */
    String getDisplayName(Path path);

    /**
     * Persists a {@link Song} to the database
     * @param song to persist
     * @return the persisted instance of {@link Song} with {@link Song#id}
     */
    Song save(Song song);

    /**
     * Retrieves a list of {@link Song} based on {@link com.madadipouya.cisapify.user.model.User#id}
     * @param userId of a User
     * @return list of Songs
     */
    List<Song> getByUserId(long userId);
}