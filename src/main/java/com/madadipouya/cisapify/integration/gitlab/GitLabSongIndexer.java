package com.madadipouya.cisapify.integration.gitlab;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.song.service.SongService;
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

    public GitLabSongIndexer(GitLabIntegration gitLabIntegration, UserService userService, SongService songService) {
        this.gitLabIntegration = gitLabIntegration;
        this.userService = userService;
        this.songService = songService;
    }

    // Run every 30 minutes
    @Scheduled(cron = "0 0/30 * * * ?")
    public void reindexAllUsersGitLabSongs() {
        logger.info("Started reindexing GitLab songs for all users");
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
        songService.deleteAll(songService.getAllByUserId(user.getId())
                .stream().filter(Song::isGitLabSourced)
                .collect(Collectors.toList()));
        songService.saveAll(gitLabIntegration.getSongs(user));
    }
}
