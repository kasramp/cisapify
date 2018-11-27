package com.madadipouya.cisapify.user.player.song.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "songs",
        indexes = {
                @Index(columnList = "name", name = "idx_song_name")},
        uniqueConstraints = @UniqueConstraint(name = "uc_song_uri", columnNames = {"uri"}))
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    @NotBlank
    @Size(min = 2, max = 4096)
    private String name;

    @Column(name = "uri", nullable = false)
    @NotBlank
    @Size(min = 16, max = 4096)
    private String uri;

    public Song() {

    }

    public Song(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
