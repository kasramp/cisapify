package com.madadipouya.cisapify.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RegExUtils;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.UUID;

public class SongUtil {

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
}
