package com.madadipouya.cisapify.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RegExUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class SongUtil {

    private static final Logger logger = LoggerFactory.getLogger(SongUtil.class);

    private static final String TMP_DIRECTORY = "/tmp";

    private static final Set<String> AUDIO_FORMATS = Set.of("aac", "flac", "m4a", "m4b", "m4p", "mp3",
            "mpc", "ogg", "oga", "wav", "wma", "webm");

    private SongUtil() {

    }

    public static boolean isAudioFile(String fileName) {
        return AUDIO_FORMATS.contains(FilenameUtils.getExtension(fileName));
    }

    public static String sanitize(String songFileName) {
        return RegExUtils.replaceAll(StringUtils.cleanPath(songFileName), "#", "");
    }

    public static String generateRandomSongFilename(String songFileName) {
        return String.format("%s.%s", UUID.randomUUID().toString(), FilenameUtils.getExtension(songFileName));
    }

    public static List<Path> getSongsInTempDirectoryOlderThan(Instant time) {
        return getAllSongsInTempDirectory().stream().filter(song -> {
            try {
                return Files.readAttributes(song, BasicFileAttributes.class).lastAccessTime().toInstant().isBefore(time);
            } catch (IOException ioException) {
                logger.warn("Failed to get last modified time of file", ioException);
                return false;
            }
        }).collect(Collectors.toList());
    }

    private static List<Path> getAllSongsInTempDirectory() {
        File[] songs = new File(TMP_DIRECTORY).listFiles((dir, file) -> isAudioFile(file));
        return songs != null ? List.of(songs).stream().map(File::toPath).collect(Collectors.toList()) : List.of();
    }
}
