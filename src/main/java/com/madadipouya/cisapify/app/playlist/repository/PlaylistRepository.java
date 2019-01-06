package com.madadipouya.cisapify.app.playlist.repository;

import com.madadipouya.cisapify.app.playlist.model.Playlist;
import com.madadipouya.cisapify.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    Set<Playlist> getByUser(User user);
}