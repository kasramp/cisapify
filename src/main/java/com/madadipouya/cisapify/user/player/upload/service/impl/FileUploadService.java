package com.madadipouya.cisapify.user.player.upload.service.impl;

import com.madadipouya.cisapify.user.player.upload.service.UploadService;
import com.madadipouya.cisapify.user.player.upload.service.exception.StorageException;
import com.madadipouya.cisapify.user.player.upload.service.exception.StorageFileNotFoundException;
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
import java.util.stream.Stream;

@Service
public class FileUploadService implements UploadService {

    private static final String path = "/tmp/";

    private final Path rootLocation;

    public FileUploadService() {
        rootLocation = Paths.get(path);
    }

    @Override
    public void store(MultipartFile file) throws StorageException {
        String filename = RegExUtils.replaceAll(StringUtils.cleanPath(file.getOriginalFilename()), "#", "");
        try {
            if (file.isEmpty()) {
                throw new StorageException(String.format("Failed to store empty file %s", filename));
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(String.format("Cannot store file with relative path outside current directory %s", filename));
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException(String.format("Failed to store file %s", filename), e);
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