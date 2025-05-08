package com.id.fileserver.api;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.id.fileserver.model.FileInfo;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileOperationsApiIT extends BaseApiIT {

    @Test
    public void getFileInfo() throws Throwable {
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
    public void createFile() throws Throwable {
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
    public void createFileNotExistingParentError() throws Throwable {
        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke("createFile", Map.of("path", "unknown/file1"), FileInfo.class)
        );

        //then
        checkFileNotExists(thrown);
    }

    @Test
    public void deleteFile() throws Throwable {
        //given
        Path dir1 = rootPath.resolve("dir1/dir2");
        Files.createDirectories(dir1);
        Path file1 = rootPath.resolve("dir1/dir2/file1");
        Files.createFile(file1);
        Path file2 = rootPath.resolve("dir1/dir2/file2");
        Files.createFile(file2);

        //when
        getClient().invoke("deleteFile", Map.of("path", "dir1/dir2/file1"), FileInfo.class);

        //then
        Path parentDir = rootPath.resolve("dir1/dir2");
        assertThat(Files.exists(parentDir) && Files.isDirectory(parentDir)).isTrue();
        String[] contents = parentDir.toFile().list();
        assertThat(contents).contains("file2");
    }

    @Test
    public void copyFile() throws Throwable {
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
    public void moveFile() throws Throwable {
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

}