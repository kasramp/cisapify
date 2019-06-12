package com.madadipouya.cisapify.util;

import org.apache.commons.io.FilenameUtils;

import java.util.Set;

public class SongUtil {

    private static Set<String> AUDIO_FORMATS = Set.of("aac", "flac", "m4a", "m4b", "m4p", "mp3",
            "mpc", "ogg", "oga", "wav", "wma", "webm");

    private SongUtil() {

    }

    public static boolean isAudioFile(String fileName) {
        return AUDIO_FORMATS.contains(FilenameUtils.getExtension(fileName));
    }
}
