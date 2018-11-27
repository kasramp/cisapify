package com.madadipouya.cisapify.user.player.song.repository;

import com.madadipouya.cisapify.user.player.song.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    Optional<Song> findByName(String songName);
}