package com.madadipouya.cisapify.app.storage.store.filesystem;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.storage.StorageType;
import com.madadipouya.cisapify.app.storage.store.AbstractSongStore;
import com.madadipouya.cisapify.app.storage.store.SongStore;
import com.madadipouya.cisapify.app.storage.store.StoredFileDetails;
import com.madadipouya.cisapify.app.storage.store.exception.StoreException;
import com.madadipouya.cisapify.app.storage.store.exception.StoreFileNotFoundException;
import com.madadipouya.cisapify.i18n.service.I18nService;
import com.madadipouya.cisapify.util.ApplicationContextUtil;
import com.madadipouya.cisapify.util.SongUtil;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
public class LocalFilesystemSongStore extends AbstractSongStore implements SongStore {

    private final Path rootLocation;

    private final I18nService i18nService;

    public LocalFilesystemSongStore(I18nService i18nService) {
        this.i18nService = i18nService;
        rootLocation = ApplicationContextUtil.getStaticSongResourcePath();
    }

    @Override
    public Resource load(Song song) throws StoreFileNotFoundException {
        try {
            return super.load(song);
        } catch (StoreException exception) {
            throw new StoreFileNotFoundException(exception);
        }
    }

    @Override
    public StoredFileDetails store(MultipartFile file) throws StoreException {
        String displayName = SongUtil.sanitize(file.getOriginalFilename());
        String storedFileName = SongUtil.generateRandomSongFilename(displayName);
        if (file.isEmpty()) {
            throw new StoreException(i18nService.getMessage("upload.service.failStoreEmptyFile", displayName));
        }
        if (displayName.contains("..")) {
            // This is a security check
            throw new StoreException(i18nService.getMessage("upload.service.fileStoreWithOutsideRelativePath", displayName));
        }
        try (InputStream inputStream = file.getInputStream()) {
            Path fileFullPath = rootLocation.resolve(storedFileName);
            Files.copy(inputStream, fileFullPath, StandardCopyOption.REPLACE_EXISTING);
            return new StoredFileDetails(storedFileName, fileFullPath.toString(), displayName);
        } catch (IOException e) {
            throw new StoreException(i18nService.getMessage("upload.service.failToStoreFile", displayName), e);
        }
    }

    @Override
    public void delete(Song song) {

    }

    @Override
    public StorageType getSupportedType() {
        return StorageType.LOCAL_SIMPLE_FILESYSTEM;
    }
}