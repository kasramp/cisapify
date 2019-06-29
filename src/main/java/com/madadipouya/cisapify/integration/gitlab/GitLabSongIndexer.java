package com.madadipouya.cisapify.integration.gitlab;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.song.service.SongService;
import com.madadipouya.cisapify.i18n.service.I18nService;
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
public class GitLabSongIndexer {

    private static final Logger logger = LoggerFactory.getLogger(GitLabSongIndexer.class);

    private final GitLabIntegration gitLabIntegration;

    private final UserService userService;

    private final SongService songService;

    private final I18nService i18nService;

    public GitLabSongIndexer(GitLabIntegration gitLabIntegration, UserService userService,
                             SongService songService, I18nService i18nService) {
        this.gitLabIntegration = gitLabIntegration;
        this.userService = userService;
        this.songService = songService;
        this.i18nService = i18nService;
    }

    // Run every 30 minutes
    @Scheduled(cron = "0 0/30 * * * ?")
    protected void reindexAllUsersGitLabSongs() {
        logger.info(i18nService.getMessage("gitlab.indexer.start"));
        List<User> users = userService.getAll();
        users.stream().filter(User::hasValidGitSettings).forEach(this::updateGitLabSongs);
    }

    @Async
    public void reindexGitLabSongsAsync(User user) {
        updateGitLabSongs(user);
    }

    void updateGitLabSongs(User user) {
        /* instead of using `user.getSongs`, we query db to avoid lazy loading exception
         * because the hibernate session is not available for this thread.
         * Hence, cannot initialize the lazy loaded songs
         */
        try {
            List<Song> songs = gitLabIntegration.getSongs(user);
            songService.deleteAll(songService.getAllByUserId(user.getId())
                    .stream().filter(Song::isGitLabSourced)
                    .collect(Collectors.toList()));
            songService.saveAll(songs);
        } catch (SongsListRetrievalException songsListRetrievalException) {
            logger.warn(i18nService.getMessage("gitlab.indexer.fail"), songsListRetrievalException);
        }

    }
}
