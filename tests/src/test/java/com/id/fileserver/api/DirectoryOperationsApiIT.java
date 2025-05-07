package com.id.fileserver.api;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.id.fileserver.model.FileInfo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DirectoryOperationsApiIT extends BaseApiIT {

    @Test
    void dirInfo() throws Throwable {
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
    public void nonExistingDirError() {
        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke("getFileInfo", Map.of("path", "dir1"), FileInfo.class)
        );

        //then
        assertThat(thrown.getCode()).isEqualTo(-32001);
        assertThat(thrown).hasMessageContaining("java.nio.file.NoSuchFileException: dir1");
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
        Path dir2 = rootPath.resolve("dir1/dir2");
        Files.createDirectories(dir2);
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

}
