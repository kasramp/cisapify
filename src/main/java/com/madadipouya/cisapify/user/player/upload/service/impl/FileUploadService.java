package com.madadipouya.cisapify.user.player.upload.service.impl;

import com.madadipouya.cisapify.user.player.song.model.Song;
import com.madadipouya.cisapify.user.player.song.repository.SongRepository;
import com.madadipouya.cisapify.user.player.upload.service.UploadService;
import com.madadipouya.cisapify.user.player.upload.service.exception.StorageException;
import com.madadipouya.cisapify.user.player.upload.service.exception.StorageFileNotFoundException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RegExUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileUploadService implements UploadService {

    private static final String path = "/tmp/";

    private final Path rootLocation;

    private final SongRepository songRepository;

    public FileUploadService(SongRepository songRepository) {
        this.songRepository = songRepository;
        rootLocation = Paths.get(path);
    }

    @Override
    public void store(MultipartFile file) throws StorageException {
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
                songRepository.save(new Song(displayName, fileFullPath.toString()));
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

    private Path loadPath(String fileName) {
        return rootLocation.resolve(fileName);
    }
}