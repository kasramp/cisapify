package com.madadipouya.cisapify.app.upload.service;

import com.madadipouya.cisapify.app.upload.service.exception.StorageException;
import com.madadipouya.cisapify.app.upload.service.exception.StorageFileNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface UploadService {

    void store(MultipartFile file) throws StorageException;

    Resource load(String fileName) throws StorageFileNotFoundException;

    Stream<Path> loadAll() throws StorageException;

    Stream<Path> loadAllForCurrentUser();
}