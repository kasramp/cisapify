package com.madadipouya.cisapify.app.storage.store;

public class StoredFileDetails {

    private final String fileName;

    private final String uri;

    private final String displayName;

    public StoredFileDetails(String fileName, String uri, String displayName) {
        this.fileName = fileName;
        this.uri = uri;
        this.displayName = displayName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUri() {
        return uri;
    }

    public String getDisplayName() {
        return displayName;
    }
}
