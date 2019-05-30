package com.madadipouya.cisapify.app.storage.store;

import com.madadipouya.cisapify.app.song.model.Song;
import com.madadipouya.cisapify.app.storage.StorageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SongStoreFactory {

    @Autowired
    private List<SongStore> stores;

    private Map<StorageType, SongStore> storesMap;

    @PostConstruct
    public void afterPropertiesSet() {
        storesMap = stores.stream().collect(Collectors.toMap(SongStore::getSupportedType, store -> store));
    }

    public SongStore getStore(Song song) {
        return storesMap.get(song.getSource());
    }

    public SongStore getStore() {
        return storesMap.get(StorageType.LOCAL_SIMPLE_FILESYSTEM);
    }
}
