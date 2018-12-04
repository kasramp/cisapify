package com.madadipouya.cisapify.app.song.repository;

import com.madadipouya.cisapify.app.song.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    Song findByFileName(String fileName);

    List<Song> findByUserId(long userId);
}