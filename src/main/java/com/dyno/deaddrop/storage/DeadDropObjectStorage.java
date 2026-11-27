package com.dyno.deaddrop.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface DeadDropObjectStorage {

    void upload(
        String storageKey,
        Path file
    ) throws IOException;

    InputStream download(
        String storageKey
    ) throws IOException;

    void delete(
        String storageKey
    ) throws IOException;
}