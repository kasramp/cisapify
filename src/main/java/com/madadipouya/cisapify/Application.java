package com.madadipouya.cisapify;

import com.madadipouya.cisapify.app.playlist.model.Playlist;
import com.madadipouya.cisapify.app.playlist.service.PlaylistService;
import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.song.service.SongService;
import com.madadipouya.cisapify.user.model.Role;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.repository.RoleRepository;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootApplication
@ComponentScan("com.madadipouya.cisapify")
@EnableJpaRepositories("com.madadipouya.cisapify")
@EntityScan("com.madadipouya.cisapify")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Component
    public class DataLoader implements ApplicationRunner {

        private UserService userService;

        private RoleRepository roleRepository;

        private SongService songService;

        private PlaylistService playlistService;

        @Autowired
        public DataLoader(UserService userService, RoleRepository roleRepository, SongService songService,
                          PlaylistService playlistService) {
            this.userService = userService;
            this.roleRepository = roleRepository;
            this.songService = songService;
            this.playlistService = playlistService;
        }

        public void run(ApplicationArguments args) {
            Role adminRole = new Role();
            adminRole.setRole("ADMIN");

            Role userRole = new Role();
            userRole.setRole("USER");

            User user = new User();
            user.setPassword("12345");
            user.setEmailAddress("kasra@madadipouya.com");
            user.setSongs(Set.of());
            user.setEnabled(true);
            user.setRoles(Set.of(adminRole));
            userService.save(user);

            User testUser = new User();
            testUser.setPassword("password");
            testUser.setEmailAddress("test@test.com");
            testUser.setEnabled(true);
            //testUser.setSongs(Set.of());
            testUser.setRoles(Set.of(userRole));
            User persistedTestUser = userService.save(testUser);
            createFakePlaylists(persistedTestUser, createFakeSongs(persistedTestUser));
        }

        private Set<Song> createFakeSongs(User user) {
            Set<Song> songs = new HashSet<>();
            for (int i = 0; i < 10; i++) {
                Song song = new Song();
                song.setUser(user);
                song.setUri(UUID.randomUUID().toString());
                song.setFileName(UUID.randomUUID().toString());
                song.setDisplayName(String.format("Song-%s", i));
                songs.add(songService.save(song));
            }
            return songs;
        }

        private void createFakePlaylists(User user, Set<Song> songs) {
            for (int i = 0; i < 3; i++) {
                Playlist playlist = new Playlist();
                playlist.setUser(user);
                playlist.setName(String.format("PlayList-%s", i));
                playlist.setSongs(songs.stream().limit(3).collect(Collectors.toSet()));
                playlistService.create(playlist);
            }
        }
    }
}