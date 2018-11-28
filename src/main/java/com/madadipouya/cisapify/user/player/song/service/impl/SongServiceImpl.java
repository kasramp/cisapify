package com.madadipouya.cisapify.user.player.song.service.impl;

import com.madadipouya.cisapify.user.player.song.repository.SongRepository;
import com.madadipouya.cisapify.user.player.song.service.SongService;

import java.nio.file.Path;

public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    public SongServiceImpl(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public String getDisplayName(Path path) {
        return songRepository.findByFileName(path.getFileName().toString()).getDisplayName();
    }
}
