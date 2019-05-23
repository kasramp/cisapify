package com.madadipouya.cisapify.integration.gitlab.listener;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.song.service.SongService;
import com.madadipouya.cisapify.integration.gitlab.GitLabIntegration;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ReindexGitLabSongsListener implements ApplicationListener<ApplicationReadyEvent> {

    private final ScheduledExecutorService executorService;

    private final GitLabIntegration gitLabIntegration;

    private final UserService userService;

    private final SongService songService;

    public ReindexGitLabSongsListener(GitLabIntegration gitLabIntegration, UserService userService, SongService songService) {
        this.gitLabIntegration = gitLabIntegration;
        this.userService = userService;
        this.songService = songService;
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // TODO Replace ExecutorService with Akka
        executorService.scheduleAtFixedRate(this::reindexGitLabSongs, 1, 60, TimeUnit.MINUTES);
    }

    private void reindexGitLabSongs() {
        userService.getAll().forEach(user -> {
            songService.deleteAll(user.getSongs().stream().filter(Song::isGitLabSourced).collect(Collectors.toList()));
            songService.saveAll(gitLabIntegration.getSongs(user.getGitlabToken(),
                    gitLabIntegration.getUserHandle(user.getGitlabToken()), user.getGitlabRepositoryName()));
        });
    }

    public void reindexGitLabSongsAsync() {
            User user = userService.getCurrentUser();
            songService.deleteAll(user.getSongs().stream().filter(Song::isGitLabSourced).collect(Collectors.toList()));
            songService.saveAll(gitLabIntegration.getSongs(user.getGitlabToken(),
                    gitLabIntegration.getUserHandle(user.getGitlabToken()), user.getGitlabRepositoryName()));
    }
}
