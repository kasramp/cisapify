package com.madadipouya.cisapify.app.song.model;

import com.madadipouya.cisapify.user.model.User;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static SongBuilder Builder() {
        return new SongBuilder();
    }

    public static class SongBuilder {

        private Song song;

        private SongBuilder() {
            song = new Song();
        }

        public SongBuilder withDisplayName(String displayName) {
            song.setDisplayName(displayName);
            return this;
        }

        public SongBuilder withFileName(String fileName) {
            song.setFileName(fileName);
            return this;
        }

        public SongBuilder withUri(String uri) {
            song.setUri(uri);
            return this;
        }

        public SongBuilder withUser(User user) {
            song.setUser(user);
            return this;
        }

        public Song build() {
            return song;
        }
    }
}
