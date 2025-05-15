package com.id.fileserver.api;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.id.fileserver.model.FileInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DirectoryOperationsApiIT extends BaseApiIT {

    @Test
    void getFileInfo() throws Throwable {
        //given
        Path dir1 = rootPath.resolve("dir1/dir2");
        Files.createDirectories(dir1);

        //when
        FileInfo result = getClient().invoke("getFileInfo", Map.of("path", "dir1/dir2"), FileInfo.class);

        //then
        var path = String.join(File.separator, "dir1", "dir2");
        FileInfo expected = FileInfo.builder()
                .name("dir2")
                .path(path)
                .size(0)
                .isDirectory(true)
                .build();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void listEmptyDirectory() throws Throwable {
        //when
        FileInfo[] result = getClient().invoke("listDirectory", Map.of("path", "."), FileInfo[].class);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    void listDirectory() throws Throwable {
        //given
        Path dir1 = rootPath.resolve("dir1/dir2");
        Files.createDirectories(dir1);
        Path file1 = rootPath.resolve("dir1/file1");
        Files.createFile(file1);

        //when
        FileInfo[] result = getClient().invoke("listDirectory", Map.of("path", "dir1"), FileInfo[].class);

        //then
        var dirPath = String.join(File.separator, "dir1", "dir2");
        FileInfo expectedDir = FileInfo.builder()
                .name("dir2")
                .path(dirPath)
                .size(0)
                .isDirectory(true)
                .build();
        var filePath = String.join(File.separator, "dir1", "file1");
        FileInfo expectedFile = FileInfo.builder()
                .name("file1")
                .path(filePath)
                .size(0)
                .isDirectory(false)
                .build();
        assertThat(result).containsExactlyInAnyOrder(expectedDir, expectedFile);
    }

    @Test
    void createDirectory() throws Throwable {
        //when
        FileInfo result = getClient().invoke("createDirectory", Map.of("path", "dir1/dir2/dir3"), FileInfo.class);

        //then
        var path = String.join(File.separator, "dir1", "dir2", "dir3");
        FileInfo expected = FileInfo.builder()
                .path(path)
                .name("dir3")
                .size(0)
                .isDirectory(true)
                .build();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void deleteDirectory() throws Throwable {
        //given
        Path dir1 = rootPath.resolve("dir1/dir2/dir3");
        Files.createDirectories(dir1);
        Path sourceFile = rootPath.resolve("dir1/dir2/dir3/sourceFile");
        Files.createFile(sourceFile);

        //when
        getClient().invoke("deleteDirectory", Map.of("path", "dir1/dir2"), FileInfo.class);

        //then
        assertThat(Files.exists(sourceFile)).isFalse();

        Path parentDir = rootPath.resolve("dir1");
        assertThat(Files.exists(parentDir)).isTrue();
        String[] contents = parentDir.toFile().list();
        assertThat(contents).isEmpty();
    }

    @Test
    void copyDirectory() throws Throwable {
        //given
        Path sourceDir = rootPath.resolve("dir11/dir12/dir13");
        Files.createDirectories(sourceDir);
        Path sourceFile = rootPath.resolve("dir11/dir12/dir13/sourceFile");
        Files.createFile(sourceFile);

        //when
        FileInfo result = getClient().invoke(
                "copyDirectory",
                Map.of("sourcePath", "dir11/dir12", "targetPath", "dir21"),
                FileInfo.class);

        //then
        FileInfo expected = FileInfo.builder()
                .path("dir21")
                .name("dir21")
                .size(0)
                .isDirectory(true)
                .build();
        assertThat(result).isEqualTo(expected);

        Path copiedFile = rootPath.resolve("dir21/dir12/dir13/sourceFile");
        assertThat(Files.exists(copiedFile)).isTrue();

        assertThat(Files.exists(sourceFile)).isTrue();
    }

    @Test
    void moveDirectory() throws Throwable {
        //given
        Path sourceDir = rootPath.resolve("dir11/dir12/sourceDir");
        Files.createDirectories(sourceDir);
        Path sourceFile = rootPath.resolve("dir11/dir12/sourceDir/sourceFile");
        Files.createFile(sourceFile);

        //when
        FileInfo result = getClient().invoke(
                "moveDirectory",
                Map.of("sourcePath", "dir11/dir12", "targetPath", "dir21"),
                FileInfo.class);

        //then
        FileInfo expected = FileInfo.builder()
                .path("dir21")
                .name("dir21")
                .size(0)
                .isDirectory(true)
                .build();
        assertThat(result).isEqualTo(expected);

        Path movedFile = rootPath.resolve("dir21/dir12/sourceDir/sourceFile");
        assertThat(Files.exists(movedFile)).isTrue();

        assertThat(Files.exists(sourceFile)).isFalse();

        //parent of moved dir exists and has no children
        Path dir11 = rootPath.resolve("dir11");
        assertThat(Files.exists(dir11)).isTrue();
        String[] contents = dir11.toFile().list();
        assertThat(contents).isEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "listDirectory", "deleteDirectory"
    })
    void notDirectoryError(String methodName) throws IOException {
        //when
        Path file = rootPath.resolve("file");
        Files.createFile(file);

        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke(methodName, Map.of("path", "file"), FileInfo.class)
        );

        //then
        checkIsNotDir(thrown);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "listDirectory", "createDirectory", "deleteDirectory"
    })
    void nullParam(String methodName) {
        //when
        Map<String, String> params = new HashMap<>();
        params.put("path", null);
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke(methodName, params, FileInfo.class)
        );

        //then
        checkParamIsNull(thrown);
    }

}
