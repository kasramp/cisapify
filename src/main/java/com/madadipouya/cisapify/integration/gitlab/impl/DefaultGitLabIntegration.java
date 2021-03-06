package com.madadipouya.cisapify.integration.gitlab.impl;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.i18n.service.I18nService;
import com.madadipouya.cisapify.integration.base.exception.SongsListRetrievalException;
import com.madadipouya.cisapify.integration.gitlab.GitLabIntegration;
import com.madadipouya.cisapify.integration.base.exception.FailRetrievingRemoteObjectException;
import com.madadipouya.cisapify.integration.gitlab.remote.response.RepositoryTreeResponse;
import com.madadipouya.cisapify.integration.gitlab.remote.response.UserResponse;
import com.madadipouya.cisapify.user.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
public class DefaultGitLabIntegration implements GitLabIntegration {

    private static final String BASE_URL = "https://gitlab.com/api/v4/%s";

    private static final String USER_URL = "user";

    private static final String PROJECT_URL = String.format(BASE_URL, "projects/%s%%2F%s/repository/tree?recursive=true");

    private static final String BLOB_URL = String.format(BASE_URL, "projects/%s%%2F%s/repository/blobs/%s/raw");

    private static final String TEMP_FULL_PATH = "/tmp/%s.mp3";

    private final RestTemplate restTemplate;

    private final I18nService i18nService;

    private DefaultGitLabIntegration(RestTemplate restTemplate, I18nService i18nService) {
        this.restTemplate = restTemplate;
        this.i18nService = i18nService;
    }

    // TODO proper exception handling
    @Override
    public String getUserHandle(String token) {
        return restTemplate.exchange(String.format(BASE_URL, USER_URL), GET, createHeader(token), UserResponse.class)
                .getBody()
                .getHandle();
    }

    @Override
    public List<Song> getSongs(User user) throws SongsListRetrievalException {
        String token = user.getGitlabToken();
        String repositoryName = user.getGitlabRepositoryName();
        String userHandle = getUserHandle(token);

        ResponseEntity<Set<RepositoryTreeResponse>> responseEntity =
                restTemplate.exchange(constructRepositoryUri(userHandle, repositoryName),
                        GET, createHeader(token), new ParameterizedTypeReference<Set<RepositoryTreeResponse>>() {
                        });

        if(!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new SongsListRetrievalException(i18nService.getMessage("gitlab.integration.failedToLoadSongsList"));
        }

        return Optional.ofNullable(responseEntity.getBody())
                .orElse(Set.of())
                .stream()
                .filter(RepositoryTreeResponse::isAudioFile)
                .map(response -> transformToSong(response, user, userHandle))
                .collect(Collectors.toList());
    }

    @Override
    public Path loadRemoteSong(String token, Song song) throws IOException, FailRetrievingRemoteObjectException {
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(toUri(song.getUri()),
                GET, createHeader(token), byte[].class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            if (Files.exists(Path.of(String.format(TEMP_FULL_PATH, song.getFileName())))) {
                return Path.of(String.format(TEMP_FULL_PATH, song.getFileName()));
            }
            return Files.write(Paths.get(String.format(TEMP_FULL_PATH, song.getFileName())), responseEntity.getBody());
        }
        throw new FailRetrievingRemoteObjectException(i18nService.getMessage("gitlab.integration.failedToLoadSongBlog",
                song.getId(), responseEntity.getStatusCode()));
    }

    private Song transformToSong(RepositoryTreeResponse response, User user, String userHandle) {
        return Song.Builder()
                .withUser(user)
                .withDisplayName(response.getName())
                .withFileName(response.getId())
                .withUri(String.format(BLOB_URL, userHandle, user.getGitlabRepositoryName(), response.getId()))
                .withGitLabSource()
                .build();
    }

    private HttpEntity createHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(APPLICATION_JSON));
        headers.set("Private-Token", token);
        return new HttpEntity<>("parameters", headers);
    }

    private URI constructRepositoryUri(String userHandle, String repositoryName) {
        return toUri(String.format(PROJECT_URL, userHandle, repositoryName));
    }

    private URI toUri(String uri) {
        return fromHttpUrl(uri).build(true).toUri();
    }
}
