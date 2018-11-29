package com.madadipouya.cisapify.user.song.service.impl;

import com.madadipouya.cisapify.user.song.model.Song;
import com.madadipouya.cisapify.user.song.repository.SongRepository;
import com.madadipouya.cisapify.user.song.service.SongService;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    public SongServiceImpl(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public String getDisplayName(Path path) {
        return songRepository.findByFileName(path.getFileName().toString()).getDisplayName();
    }

    @Override
    public Song save(Song song) {
        return songRepository.save(song);
    }
}
