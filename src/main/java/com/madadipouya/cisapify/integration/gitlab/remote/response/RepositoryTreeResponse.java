package com.madadipouya.cisapify.integration.gitlab.remote.response;

import org.apache.commons.io.FilenameUtils;

import java.util.Set;

public class RepositoryTreeResponse {

    private static Set<String> AUDIO_FORMATS = Set.of("aac", "flac", "m4a", "m4b", "m4p", "mp3",
            "mpc", "ogg", "oga", "wav", "wma", "webm");

    private String id;

    private String name;

    private String type;

    private String path;

    private String mode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    private boolean isFile() {
        return FileType.BLOB.toString().equalsIgnoreCase(type);
    }

    public boolean isAudioFile() {
        return isFile() && AUDIO_FORMATS.contains(FilenameUtils.getExtension(name));
    }

    public boolean isDirectory() {
        return !isFile();
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof RepositoryTreeResponse)) {
            return false;
        }
        RepositoryTreeResponse repositoryTreeResponse = (RepositoryTreeResponse) o;
        return repositoryTreeResponse.getId().equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private enum FileType {
        BLOB, TREE
    }
}
