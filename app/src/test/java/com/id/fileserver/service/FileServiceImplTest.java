package com.id.fileserver.service;

import com.id.fileserver.model.FileInfo;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileServiceImplTest {

    private final Path rootPath = Paths.get("./unit-root-dir").toAbsolutePath().normalize();

    private final ConcurrentMap<Path, ReentrantLock> locks = new ConcurrentHashMap<>();
    private final FileServiceImpl service = new FileServiceImpl(rootPath, locks);

    @BeforeEach
    void beforeEach() throws IOException {
        if (!Files.exists(rootPath)) {
            Files.createDirectories(rootPath);
        }
        FileUtils.cleanDirectory(rootPath.toFile());
    }

    @Test
    void pathOutOfRootDirectoryAccessError() {
        String[] paths = {"/", "../..", "../../../"};
        for (String path : paths) {
            SecurityException thrown = assertThrows(
                    SecurityException.class,
                    () -> service.getFileInfo(path));

            assertThat(thrown).hasMessageContaining("Access forbidden");
        }
    }

    @Test
    void pathDoesNotExistsError() {
        //when
        NoSuchFileException thrown = assertThrows(
                NoSuchFileException.class,
                () -> service.copyFile("sourceFile", "sourceFile"));

        //then
        assertThat(thrown).hasMessageContaining("sourceFile");
    }

    @Test
    void notDirectoryError() throws IOException {
        //given
        Path file = rootPath.resolve("file");
        Files.createFile(file);

        //when
        IOException thrown = assertThrows(
                IOException.class,
                () -> service.listDirectory("file"));

        //then
        assertThat(thrown).hasMessageContaining("Not a directory");
    }

    @Test
    void getFileInfo() throws IOException {
        FileInfo fileInfo = service.getFileInfo(".");

        FileInfo expected = FileInfo.builder()
                .path("")
                .name("unit-root-dir")
                .size(0)
                .isDirectory(true)
                .build();
        assertThat(fileInfo).isEqualTo(expected);
    }

    @Test
    void testCleanup1() throws IOException {
        //given: 3 files
        Path file1 = rootPath.resolve("file1");
        Path file2 = rootPath.resolve("file2");
        Path file3 = rootPath.resolve("file3");
        Files.createFile(file1);
        Files.createFile(file2);
        Files.createFile(file3);
        int fileNum = 3;

        //when: append some data to them
        var line = "a".repeat(50);
        int lineNum = 1_000;
        for (int i = 0; i < lineNum; i++) {
            String data = line + i + System.lineSeparator();
            for (int j = 1; j <= fileNum; j++) {
                service.appendToFile("file" + j, data);
            }
        }

        //then
        checkLines(file1, line, lineNum);
        checkLines(file2, line, lineNum);
        checkLines(file3, line, lineNum);

        //and
        Files.delete(file2);
        Files.delete(file3);
        service.onScheduled();

        //then
        assertThat(locks).hasSize(1);
        assertThat(locks.get(file1)).isNotNull();
        assertThat(locks.get(file2)).isNull();
        assertThat(locks.get(file3)).isNull();
    }

    @Test
    void testCleanup2() throws Throwable {
        //when: create N nested dirs with M files in each dir
        var path = "";
        int dirNum = 5;
        int fileNum = 3;
        var line = "line";
        for (int i = 0; i < dirNum; i++) {
            path += "dir%d/".formatted(i);
            service.createDirectory(path);
            for (int j = 0; j < fileNum; j++) {
                var file = path + "file%d".formatted(j);
                service.createFile(file);
                String data = line + j + System.lineSeparator();
                service.appendToFile(file, data);
            }
        }

        //then: deepest file exists
        Path deepestFile = rootPath.resolve(path + "file" + (fileNum - 1));
        assertThat(Files.exists(deepestFile)).isTrue();

        //when: delete intermediate
        Path dir1 = rootPath.resolve("dir0/dir1");
        Path file1 = rootPath.resolve("dir0/file0");
        Path file2 = rootPath.resolve("dir0/file1");
        Path file3 = rootPath.resolve("dir0/file2");

        FileUtils.deleteDirectory(dir1.toFile());
        service.onScheduled();

        //then
        assertThat(locks).hasSize(3);
        assertThat(locks.get(file1)).isNotNull();
        assertThat(locks.get(file2)).isNotNull();
        assertThat(locks.get(file3)).isNotNull();
    }

    private void checkLines(Path file1, String line, int lineNum) throws IOException {
        //total line number is correct
        List<String> contents = FileUtils.readLines(file1.toFile(), StandardCharsets.UTF_8);
        assertThat(contents.size()).isEqualTo(lineNum);

        //first line and last line is correct
        Collections.sort(contents);
        assertThat(contents.getFirst()).isEqualTo(line + "0");
        assertThat(contents.getLast()).isEqualTo(line + (lineNum - 1));
    }

}