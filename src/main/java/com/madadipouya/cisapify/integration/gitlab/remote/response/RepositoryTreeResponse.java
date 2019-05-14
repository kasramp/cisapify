package com.madadipouya.cisapify.integration.gitlab.remote.response;

public class RepositoryTreeResponse {

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

    public boolean isFile() {
        return FileType.BLOB.toString().equalsIgnoreCase(type);
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


    private enum FileType {
        BLOB, TREE
    }
}
