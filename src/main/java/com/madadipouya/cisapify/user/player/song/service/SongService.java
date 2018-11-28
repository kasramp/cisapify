package com.madadipouya.cisapify.user.player.song.service;

import java.nio.file.Path;
import com.madadipouya.cisapify.user.player.song.model.Song;

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
}
