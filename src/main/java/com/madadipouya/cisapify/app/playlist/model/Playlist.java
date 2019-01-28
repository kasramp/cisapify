package com.madadipouya.cisapify.app.playlist.model;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "playlists",
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_playlist_name", columnNames = "name")
        })
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    @NotBlank
    @Size(min = 2, max = 4096)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "playlist_song", joinColumns = @JoinColumn(name = "playlist_id"), inverseJoinColumns = @JoinColumn(name = "song_id"))
    private Set<Song> songs;


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
    }
}
