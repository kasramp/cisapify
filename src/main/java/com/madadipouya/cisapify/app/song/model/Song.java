package com.madadipouya.cisapify.app.song.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "songs",
        indexes = {
                @Index(columnList = "file_name", name = "idx_song_file_name")},
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_song_uri", columnNames = {"uri"}),
                @UniqueConstraint(name = "uc_song_file_name", columnNames = {"file_name"})
        })
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "display_name", nullable = false)
    @NotBlank
    @Size(min = 2, max = 4096)
    private String displayName;

    @Column(name = "file_name", nullable = false)
    @NotBlank
    @Size(max = 128)
    private String fileName;

    @Column(name = "uri", nullable = false)
    @NotBlank
    @Size(min = 16, max = 4096)
    private String uri;

    public Song() {

    }

    public Song(String displayName, String fileName, String uri) {
        this.fileName = fileName;
        this.displayName = displayName;
        this.uri = uri;
    }

    public long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
