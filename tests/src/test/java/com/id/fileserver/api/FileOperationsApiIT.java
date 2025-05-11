package com.id.fileserver.api;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.id.fileserver.model.FileInfo;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileOperationsApiIT extends BaseApiIT {

    @Test
    void getFileInfo() throws Throwable {
        //given
        Path file1 = rootPath.resolve("file1");
        FileUtils.writeStringToFile(file1.toFile(), "123", StandardCharsets.UTF_8);

        //when
        FileInfo result = getClient().invoke("getFileInfo", Map.of("path", "file1"), FileInfo.class);

        //then
        FileInfo expected = FileInfo.builder()
                .path("file1")
                .name("file1")
                .size(3)
                .isDirectory(false)
                .build();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void createFile() throws Throwable {
        //given
        Path dir = rootPath.resolve("dir1/dir2");
        Files.createDirectories(dir);

        //when
        FileInfo result = getClient().invoke("createFile", Map.of("path", "dir1/dir2/file1"), FileInfo.class);

        //then
        FileInfo expected = FileInfo.builder()
                .path("dir1\\dir2\\file1")
                .name("file1")
                .size(0)
                .isDirectory(false)
                .build();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void deleteFile() throws Throwable {
        //given
        Path parentDir = rootPath.resolve("dir1/dir2");
        Files.createDirectories(parentDir);
        Path file1 = rootPath.resolve("dir1/dir2/file1");
        Files.createFile(file1);
        Path file2 = rootPath.resolve("dir1/dir2/file2");
        Files.createFile(file2);

        //when
        getClient().invoke("deleteFile", Map.of("path", "dir1/dir2/file1"), FileInfo.class);

        //then
        assertThat(Files.exists(file1)).isFalse();

        assertThat(Files.exists(parentDir)).isTrue();
        assertThat(Files.exists(file2)).isTrue();
    }

    @Test
    void copyFile() throws Throwable {
        //given
        Path targetDir = rootPath.resolve("dir1/dir2");
        Files.createDirectories(targetDir);
        Path sourceFile = rootPath.resolve("dir1/sourceFile");
        Files.createFile(sourceFile);

        //when
        FileInfo result = getClient().invoke(
                "copyFile",
                Map.of("sourcePath", "dir1/sourceFile", "targetPath", "dir1/dir2/sourceFile"),
                FileInfo.class);

        //then
        FileInfo expected = FileInfo.builder()
                .path("dir1\\dir2\\sourceFile")
                .name("sourceFile")
                .size(0)
                .isDirectory(false)
                .build();
        assertThat(result).isEqualTo(expected);
        Path targetFile = rootPath.resolve("dir1/dir2/sourceFile");
        assertThat(Files.exists(targetFile)).isTrue();

        assertThat(Files.exists(sourceFile)).isTrue();
    }

    @Test
    void moveFile() throws Throwable {
        //given
        Path targetDir = rootPath.resolve("dir1/dir2");
        Files.createDirectories(targetDir);
        Path sourceFile = rootPath.resolve("dir1/sourceFile");
        Files.createFile(sourceFile);

        //when
        FileInfo result = getClient().invoke(
                "moveFile",
                Map.of("sourcePath", "dir1/sourceFile", "targetPath", "dir1/dir2/sourceFile"),
                FileInfo.class);

        //then
        FileInfo expected = FileInfo.builder()
                .path("dir1\\dir2\\sourceFile")
                .name("sourceFile")
                .size(0)
                .isDirectory(false)
                .build();
        assertThat(result).isEqualTo(expected);
        Path targetFile = rootPath.resolve("dir1/dir2/sourceFile");
        assertThat(Files.exists(targetFile)).isTrue();

        assertThat(Files.exists(sourceFile)).isFalse();
    }

    @Test
    void appendToFile() throws Throwable {
        //given
        Path file1 = rootPath.resolve("file1");
        Files.createFile(file1);

        //when
        getClient().invoke(
                "appendToFile",
                Map.of("path", "file1", "data", "line1\nline2\n"),
                Void.class);

        //then
        String expected = FileUtils.readFileToString(file1.toFile(), StandardCharsets.UTF_8);
        assertThat(expected).isEqualTo("line1\nline2\n");
    }

    @Test
    void appendToFileArgError() throws Throwable {
        //given
        Path file1 = rootPath.resolve("file1");
        Files.createFile(file1);

        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke(
                        "appendToFile",
                        Map.of("path", "file1", "data", ""),
                        Void.class));

        //then
        assertThat(thrown.getCode()).isEqualTo(-32098);
        assertThat(thrown).hasMessageContaining("Invalid data");
    }

    @Test
    void readFromFile() throws Throwable {
        //given
        Path file1 = rootPath.resolve("file1");
        FileUtils.writeStringToFile(file1.toFile(), "line1\nline2\n", StandardCharsets.UTF_8);

        //when
        String result = getClient().invoke(
                "readFromFile",
                Map.of("path", "file1", "offset", 0, "length", 50),
                String.class);

        //then
        assertThat(result).isEqualTo("line1\nline2\n");

        //when
        result = getClient().invoke(
                "readFromFile",
                Map.of("path", "file1", "offset", 2, "length", 3),
                String.class);

        //then
        assertThat(result).isEqualTo("ne1");
    }

    @Test
    void readFromFileArgError() throws Throwable {
        //given
        Path file1 = rootPath.resolve("file1");
        Files.createFile(file1);

        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke(
                        "readFromFile",
                        Map.of("path", "file1", "offset", -1, "length", 42),
                        String.class)
        );

        //then
        assertThat(thrown.getCode()).isEqualTo(-32098);
        assertThat(thrown).hasMessageContaining("Invalid offset");

        //when
        thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke(
                        "readFromFile",
                        Map.of("path", "file1", "offset", 0, "length", 0),
                        String.class)
        );

        //then
        assertThat(thrown.getCode()).isEqualTo(-32098);
        assertThat(thrown).hasMessageContaining("Invalid length");
    }

    @Test
    void notFileError() throws IOException {
        //given
        Path dir = rootPath.resolve("dir");
        Files.createDirectories(dir);

        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke("deleteFile", Map.of("path", "dir"), FileInfo.class)
        );

        //then
        checkIsNotFile(thrown);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "getFileInfo", "createFile", "deleteFile"
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