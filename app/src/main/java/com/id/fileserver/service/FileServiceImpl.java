package com.id.fileserver.service;

import com.id.fileserver.model.FileInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final Path rootPath;

    @Override
    public FileInfo getFileInfo(String relativePath) throws IOException {
        Path path = resolvePath(relativePath);
        checkExists(relativePath, path);
        return createFileInfo(path);
    }

    @Override
    public List<FileInfo> listDirectory(String relativePath) throws IOException {
        Path path = resolvePath(relativePath);
        checkExists(relativePath, path);
        if (Files.exists(path) && !Files.isDirectory(path)) {
            throw new NotDirectoryException(relativePath);
        }

        try (Stream<Path> paths = Files.list(path)) {
            return paths.map(p -> {
                try {
                    return createFileInfo(p);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
        }
    }

    @Override
    public FileInfo createFile(String relativePath) throws IOException {
        Path path = resolvePath(relativePath);
        Path parentPath = resolvePath(path.getParent().toString());

        Path normalizedPath = Paths.get(relativePath).normalize();
        checkExists(normalizedPath.getParent().toString(), parentPath);
        Files.createFile(path);
        return createFileInfo(path);
    }

    @Override
    public FileInfo createDirectory(String relativePath) throws IOException {
        Path path = resolvePath(relativePath);
        Path result = Files.createDirectories(path);
        return createFileInfo(result);
    }

    @Override
    public boolean deleteFile(String relativePath) throws IOException {
        Path path = resolvePath(relativePath);
        checkExists(relativePath, path);
        Files.delete(path);
        return true;
    }

    @Override
    public boolean deleteDirectory(String relativePath) throws IOException {
        Path path = resolvePath(relativePath);
        checkExists(relativePath, path);
        if (!Files.isDirectory(path)) {
            throw new NotDirectoryException(relativePath);
        }

        try (Stream<Path> walk = Files.walk(path)) {
            // delete children first
            walk.sorted((a, b) -> b.compareTo(a))
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        return true;
    }

    @Override
    public FileInfo moveFile(String sourcePath, String targetPath) throws IOException {
        Path source = resolvePath(sourcePath);
        checkExists(sourcePath, source);
        Path target = resolvePath(targetPath);

        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        return createFileInfo(target);
    }

    @Override
    public FileInfo copyFile(String sourcePath, String targetPath) throws IOException {
        Path source = resolvePath(sourcePath);
        checkExists(sourcePath, source);
        Path target = resolvePath(targetPath);

        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        return createFileInfo(target);
    }

    @Override
    public FileInfo copyDirectory(String sourcePath, String targetPath) throws IOException {
        Path source = resolvePath(sourcePath);
        checkExists(sourcePath, source);
        Path target = resolvePath(targetPath);

        FileUtils.copyDirectory(source.toFile(), target.toFile());
        return createFileInfo(target);
    }

    @Override
    public boolean appendData(String relativePath, String data) throws IOException {
        throw new RuntimeException("not supported yet");
    }

    @Override
    public String readData(String relativePath, int offset, int length) throws IOException {
        throw new RuntimeException("not supported yet");
    }

    private FileInfo createFileInfo(Path path) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        return FileInfo.builder()
                .name(path.getFileName().toString())
                .path(rootPath.relativize(path).toString())
                .size(attrs.size())
                .isDirectory(attrs.isDirectory())
                .build();
    }

    private Path resolvePath(String relativePath) {
        Path normalizedPath = Paths.get(relativePath).normalize();
        Path resolvedPath = rootPath.resolve(normalizedPath);

        //path must be within the root dir
        if (!resolvedPath.toAbsolutePath().startsWith(rootPath.toAbsolutePath())) {
            throw new SecurityException("Access forbidden");
        }
        return resolvedPath;
    }

    private void checkExists(String relativePath, Path resolvedPath) throws NoSuchFileException {
        if (!Files.exists(resolvedPath)) {
            throw new NoSuchFileException(relativePath);
        }
    }
}