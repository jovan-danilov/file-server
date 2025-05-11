package com.id.fileserver.service;

import com.id.fileserver.model.FileInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final Path rootPath;
    private final ConcurrentMap<Path, ReentrantLock> fileLocks = new ConcurrentHashMap<>();

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
        checkIsDirectory(relativePath, path);

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
        Path parentDir = normalizedPath.getParent();
        if (parentDir != null) {
            checkExists(parentDir.toString(), parentPath);
        }
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
    public void deleteFile(String relativePath) throws IOException {
        Path path = resolvePath(relativePath);
        checkExists(relativePath, path);
        checkIsFile(relativePath, path);
        Files.delete(path);
    }

    @Override
    public void deleteDirectory(String relativePath) throws IOException {
        Path path = resolvePath(relativePath);
        checkExists(relativePath, path);
        checkIsDirectory(relativePath, path);

        FileUtils.deleteDirectory(path.toFile());
    }

    @Override
    public FileInfo moveFile(String sourcePath, String targetPath) throws IOException {
        Path source = resolvePath(sourcePath);
        checkExists(sourcePath, source);
        checkIsFile(sourcePath, source);
        Path target = resolvePath(targetPath);

        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        return createFileInfo(target);
    }

    @Override
    public FileInfo moveDirectory(String sourcePath, String targetPath) throws IOException {
        Path source = resolvePath(sourcePath);
        checkExists(sourcePath, source);
        checkIsDirectory(sourcePath, source);
        Path target = resolvePath(targetPath);

        FileUtils.moveDirectoryToDirectory(source.toFile(), target.toFile(), true);
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
        checkIsDirectory(sourcePath, source);
        Path target = resolvePath(targetPath);

        FileUtils.copyDirectoryToDirectory(source.toFile(), target.toFile());
        return createFileInfo(target);
    }

    @Override
    public void appendToFile(String relativePath, String data) throws IOException {
        Path path = resolvePath(relativePath);
        checkExists(relativePath, path);
        checkIsFile(relativePath, path);
        if (data == null) {
            throw new IllegalArgumentException("Null param");
        }
        if (data.length() >= 10_000 || data.isEmpty()) {
            throw new IllegalArgumentException("Invalid data");
        }

        ReentrantLock lock = fileLocks.computeIfAbsent(path, k -> new ReentrantLock());
        lock.lock();
        try {
            FileUtils.writeStringToFile(path.toFile(), data, StandardCharsets.UTF_8, true);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String readFromFile(String relativePath, int offset, int length) throws IOException {
        Path path = resolvePath(relativePath);
        checkExists(relativePath, path);
        checkIsFile(relativePath, path);

        if (offset < 0) {
            throw new IllegalArgumentException("Invalid offset");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Invalid length");
        }

        try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "r")) {
            file.seek(offset);
            byte[] dataBytes = new byte[length];
            int bytesRead = file.read(dataBytes, 0, length);

            if (bytesRead < 0) {
                return new String(new byte[0], StandardCharsets.UTF_8); // EOF
            }
            if (bytesRead < length) {
                length = bytesRead;
            }
            return new String(dataBytes, 0, length, StandardCharsets.UTF_8);
        }
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
        if (relativePath == null) {
            throw new IllegalArgumentException("Null param");
        }
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

    private void checkIsFile(String relativePath, Path resolvedPath) throws IOException {
        if (!Files.isRegularFile(resolvedPath)) {
            throw new IOException("Not a file: " + relativePath);
        }
    }

    private void checkIsDirectory(String relativePath, Path resolvedPath) throws IOException {
        if (!Files.isDirectory(resolvedPath)) {
            throw new IOException("Not a directory: " + relativePath);
        }
    }
}