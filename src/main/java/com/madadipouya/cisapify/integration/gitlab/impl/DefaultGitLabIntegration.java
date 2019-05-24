package com.madadipouya.cisapify.integration.gitlab.impl;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.integration.gitlab.GitLabIntegration;
import com.madadipouya.cisapify.integration.gitlab.exception.FailRetrievingRemoteObjectException;
import com.madadipouya.cisapify.integration.gitlab.remote.response.RepositoryTreeResponse;
import com.madadipouya.cisapify.integration.gitlab.remote.response.UserResponse;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class DefaultGitLabIntegration implements GitLabIntegration {

    private static final String BASE_URL = "https://gitlab.com/api/v4/%s";

    private static final String USER_URL = "user";

    private static final String PROJECT_URL = String.format(BASE_URL, "projects/%s%%2F%s/repository/tree?recursive=true");

    private static final String BLOB_URL = String.format(BASE_URL, "projects/%s%%2F%s/repository/blobs/%s/raw");

    private final RestTemplate restTemplate;

    private final UserService userService;

    //https://gitlab.com/api/v4/projects/kasramp%2Fmz/repository/blobs/e8e6c02267331817da62defde1ec8363c200fdc0/raw

    // https://gitlab.com/api/v4/projects/kasramp%2Fmz/repository/tree?recursive=true
    // https://gitlab.com/api/v4/projects/kasramp%2Fmz/repository/tree?path=Singles


    private DefaultGitLabIntegration(UserService userService, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.userService = userService;
    }

    // TODO proper exception handling
    @Override
    public String getUserHandle(String token) {
        return restTemplate.exchange(String.format(BASE_URL, USER_URL), HttpMethod.GET, createHeader(token), UserResponse.class).getBody().getHandle();
    }

    public List<Song> getSongs(String token, String handle, String repositoryName) {
        List<Song> songs = new ArrayList<>();
        userService.getLoggedInUser().ifPresent(user -> {
            URI uri = UriComponentsBuilder.fromHttpUrl(String.format(PROJECT_URL, handle, repositoryName)).build(true).toUri();
            ResponseEntity<Set<RepositoryTreeResponse>> responseEntity = restTemplate.exchange(uri,
                    HttpMethod.GET, createHeader(token), new ParameterizedTypeReference<Set<RepositoryTreeResponse>>() {
                    });
            songs.addAll(Optional.ofNullable(responseEntity.getBody())
                    .orElse(Set.of())
                    .stream()
                    .filter(RepositoryTreeResponse::isAudioFile)
                    .map(repositoryTreeResponse -> Song.Builder()
                            .withUser(user)
                            .withDisplayName(repositoryTreeResponse.getName())
                            .withFileName(repositoryTreeResponse.getId())
                            .withUri(String.format(BLOB_URL, handle, repositoryName, repositoryTreeResponse.getId()))
                            .build())
                    .collect(Collectors.toList()));
        });
        return List.copyOf(songs);
    }

    public Path loadRemoteSong(String token, Song song) throws IOException, FailRetrievingRemoteObjectException {
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(UriComponentsBuilder.fromHttpUrl(song.getUri()).build(true).toUri(),
                HttpMethod.GET, createHeader(token), byte[].class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            if(Files.exists(Path.of(String.format("/tmp/%s.mp3", song.getFileName())))) {
                return Path.of(String.format("/tmp/%s.mp3", song.getFileName()));
            }
            return Files.write(Paths.get(String.format("/tmp/%s.mp3", song.getFileName())), responseEntity.getBody());
        }
        throw new FailRetrievingRemoteObjectException(String.format("Unable to retrieve song blob for song id: %s. Got response: %s",
                song.getId(), responseEntity.getStatusCode()));
    }

    private HttpEntity createHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(APPLICATION_JSON));
        headers.set("Private-Token", token);
        return new HttpEntity<>("parameters", headers);
    }
}
