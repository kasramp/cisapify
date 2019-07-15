package com.madadipouya.cisapify.app.song.job;

import com.madadipouya.cisapify.i18n.service.I18nService;
import com.madadipouya.cisapify.util.SongUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class CachedFilesCleanerJob {

    private static final Logger logger = LoggerFactory.getLogger(CachedFilesCleanerJob.class);

    private final I18nService i18nService;

    public CachedFilesCleanerJob(I18nService i18nService) {
        this.i18nService = i18nService;
    }

    // Run every 2 hours
    @Scheduled(cron = "0 0/120 * * * ?")
    protected void deleteSongsInTempDirectoryOlderThanTwoHours() {
        logger.info(i18nService.getMessage("song.job.cachedFilesCleaner.start"));
        List<Path> songsToDelete = SongUtil.getSongsInTempDirectoryOlderThan(getTwoHoursAgoTime());
        songsToDelete.forEach(this::deleteSong);
    }

    private Instant getTwoHoursAgoTime() {
        return Instant.now().minus(Duration.ofHours(2));
    }

    private void deleteSong(Path song) {
        try {
            Files.deleteIfExists(song);
        } catch (IOException fileDeletionException) {
            logger.warn(i18nService.getMessage("song.job.cachedFilesCleaner.fileDeletionFailure",
                    song.getFileName()), fileDeletionException);
        }
    }
}
