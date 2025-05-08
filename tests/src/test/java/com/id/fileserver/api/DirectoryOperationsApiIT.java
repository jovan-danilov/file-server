package com.id.fileserver.api;

import com.id.fileserver.model.FileInfo;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectoryOperationsApiIT extends BaseApiIT {

    @Test
    void getFileInfo() throws Throwable {
        //given
        Path dir1 = rootPath.resolve("dir1/dir2");
        Files.createDirectories(dir1);

        //when
        FileInfo result = getClient().invoke("getFileInfo", Map.of("path", "dir1/dir2"), FileInfo.class);

        //then
        FileInfo expected = FileInfo.builder()
                .name("dir2")
                .path("dir1\\dir2")
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
        FileInfo expectedDir = FileInfo.builder()
                .name("dir2")
                .path("dir1\\dir2")
                .size(0)
                .isDirectory(true)
                .build();
        FileInfo expectedFile = FileInfo.builder()
                .name("file1")
                .path("dir1\\file1")
                .size(0)
                .isDirectory(false)
                .build();
        assertThat(result).containsExactlyInAnyOrder(expectedDir, expectedFile);
    }

    @Test
    public void createDirectory() throws Throwable {
        //when
        FileInfo result = getClient().invoke("createDirectory", Map.of("path", "dir1/dir2/dir3"), FileInfo.class);

        //then
        FileInfo expected = FileInfo.builder()
                .path("dir1\\dir2\\dir3")
                .name("dir3")
                .size(0)
                .isDirectory(true)
                .build();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void deleteDirectory() throws Throwable {
        //given
        Path dir1 = rootPath.resolve("dir1/dir2/dir3");
        Files.createDirectories(dir1);

        //when
        getClient().invoke("deleteDirectory", Map.of("path", "dir1/dir2"), FileInfo.class);

        //then
        Path parentDir = rootPath.resolve("dir1");
        assertThat(Files.exists(parentDir) && Files.isDirectory(parentDir)).isTrue();
        String[] contents = parentDir.toFile().list();
        assertThat(contents).isEmpty();
    }

    @Test
    public void copyDirectory() throws Throwable {
        //given
        Path sourceDir = rootPath.resolve("dir11/dir12/dir13");
        Files.createDirectories(sourceDir);
        Path file1 = rootPath.resolve("dir11/dir12/dir13/file1");
        Files.createFile(file1);

        Path targetDir = rootPath.resolve("dir21");
        Files.createDirectories(targetDir);

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

        Path dir13 = rootPath.resolve("dir21/dir13");
        assertThat(Files.exists(dir13) && Files.isDirectory(dir13)).isTrue();

        String[] contents = dir13.toFile().list();
        assertThat(contents).contains("file1");
        assertThat(Files.exists(file1)).isTrue();
    }


}
