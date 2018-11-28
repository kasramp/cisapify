package com.madadipouya.cisapify.user.player.song.repository;

import com.madadipouya.cisapify.user.player.song.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    Song findByFileName(String fileName);
}