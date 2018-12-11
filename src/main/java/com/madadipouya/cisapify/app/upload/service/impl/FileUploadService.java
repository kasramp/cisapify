package com.madadipouya.cisapify.app.upload.service.impl;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.song.service.SongService;
import com.madadipouya.cisapify.app.upload.service.UploadService;
import com.madadipouya.cisapify.app.upload.service.exception.StorageException;
import com.madadipouya.cisapify.app.upload.service.exception.StorageFileNotFoundException;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.service.UserService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RegExUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileUploadService implements UploadService {

    private static final String path = "/tmp/";

    private final Path rootLocation;

    private final SongService songService;

    private final UserService userService;

    public FileUploadService(SongService songService, UserService userService) {
        this.songService = songService;
        this.userService = userService;
        rootLocation = Paths.get(path);
    }

    @Override
    public void store(MultipartFile file, String emailAddress) throws StorageException {
        User user = userService.getUserByEmailAddress(emailAddress).orElseThrow(() -> new UsernameNotFoundException("Couldn't find user"));
        String displayName = RegExUtils.replaceAll(StringUtils.cleanPath(file.getOriginalFilename()), "#", "");

        String storedFileName = String.format("%s.%s", UUID.randomUUID().toString(), FilenameUtils.getExtension(displayName));

        try {
            if (file.isEmpty()) {
                throw new StorageException(String.format("Failed to store empty file %s", displayName));
            }
            if (displayName.contains("..")) {
                // This is a security check
                throw new StorageException(String.format("Cannot store file with relative path outside current directory %s", displayName));
            }
            try (InputStream inputStream = file.getInputStream()) {
                Path fileFullPath = rootLocation.resolve(storedFileName);
                Files.copy(inputStream, fileFullPath, StandardCopyOption.REPLACE_EXISTING);
                songService.save(Song.Builder().withDisplayName(displayName).withFileName(storedFileName)
                        .withUri(fileFullPath.toString())
                        .withUser(user)
                        .build());
            }
        } catch (IOException e) {
            throw new StorageException(String.format("Failed to store file %s", displayName), e);
        }
    }

    @Override
    public Resource load(String filename) throws StorageFileNotFoundException {
        try {
            Path file = loadPath(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(String.format("Could not read file: %s", filename));
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException(String.format("Could not read file: %s", filename), e);
        }
    }

    @Override
    public Stream<Path> loadAll() throws StorageException {
        try {
            return Files.walk(rootLocation, 1)
                    .filter(path -> !path.equals(rootLocation) && path.getFileName().toString().endsWith(".mp3"))
                    .map(rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Set<Song> loadAllForUserEmail(String emailAddress) {
        User user = userService.getUserByEmailAddress(emailAddress)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Can't retrieve User details for %s email", emailAddress)));
        return user.getSongs().stream().filter(song -> Files.exists(Paths.get(song.getUri()))).collect(Collectors.toSet());
    }

    private Path loadPath(String fileName) {
        return rootLocation.resolve(fileName);
    }
}