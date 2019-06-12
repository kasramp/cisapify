package com.madadipouya.cisapify.integration.dropbox;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.song.service.SongService;
import com.madadipouya.cisapify.integration.base.exception.SongsListRetrievalException;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DropboxSongIndexer {

    private static final Logger logger = LoggerFactory.getLogger(DropboxSongIndexer.class);

    private final DropboxIntegration dropboxIntegration;

    private final UserService userService;

    private final SongService songService;

    public DropboxSongIndexer(DropboxIntegration dropboxIntegration, UserService userService, SongService songService) {
        this.dropboxIntegration = dropboxIntegration;
        this.userService = userService;
        this.songService = songService;
    }

    // Run every 30 minutes
    @Scheduled(cron = "0 0/30 * * * ?")
    protected void reindexAllUsersDropboxSongs() {
        logger.info("Started reindexing Dropbox songs for all users");
        List<User> users = userService.getAll();
        users.stream().filter(User::hasValidDropboxToken).forEach(this::updateDropboxSongs);
    }

    @Async
    public void reindexDropboxSongsAsync(User user) {
        updateDropboxSongs(user);
    }

    void updateDropboxSongs(User user) {
        /* instead of using `user.getSongs`, we query db to avoid lazy loading exception
         * because the hibernate session is not available for this thread.
         * Hence, cannot initialize the lazy loaded songs
         */
        try {
            List<Song> songs = dropboxIntegration.getSongs(user);
            songService.deleteAll(songService.getAllByUserId(user.getId())
                    .stream().filter(Song::isDropboxSourced)
                    .collect(Collectors.toList()));
            songService.saveAll(songs);
        } catch (SongsListRetrievalException songsListRetrievalException) {
            logger.warn("Indexer fail to get songs list of Dropbox", songsListRetrievalException);
        }
    }
}
