package ru.scheduler.users.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init();

    void store(MultipartFile file, String fileName);

    Stream<Path> loadAll();

    void delete(String path) throws IOException;

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();
}
