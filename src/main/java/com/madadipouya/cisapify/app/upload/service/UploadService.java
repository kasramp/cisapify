package com.madadipouya.cisapify.app.upload.service;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.upload.service.exception.StorageException;
import com.madadipouya.cisapify.app.upload.service.exception.StorageFileNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

public interface UploadService {

    void store(MultipartFile file, String emailAddress) throws StorageException;

    Resource load(String fileName) throws StorageFileNotFoundException;

    Stream<Path> loadAll() throws StorageException;

    Set<Song> loadAllForUserEmail(String emailAddress);
}