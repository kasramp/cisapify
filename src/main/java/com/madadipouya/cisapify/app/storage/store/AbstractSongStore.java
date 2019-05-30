package com.madadipouya.cisapify.app.storage.store;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.storage.store.exception.StoreException;
import com.madadipouya.cisapify.app.storage.store.exception.StoreFileNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Path;

public abstract class AbstractSongStore implements SongStore {

    public Resource load(Song song) throws StoreException {
        return load(Path.of(song.getUri()));
    }

    protected Resource load(Path path) throws StoreException {
        try {
            Resource resource = new UrlResource(path.toUri());
            if (!isValidResource(resource)) {
                throw new StoreFileNotFoundException(String.format("File (%s) now found", path.getFileName()));
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new StoreFileNotFoundException(String.format("File (%s) now found", path.getFileName()), e);
        }
    }

    private boolean isValidResource(Resource resource) {
        return resource.exists() || resource.isReadable();
    }
}
