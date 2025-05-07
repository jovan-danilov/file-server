package com.id.fileserver.api;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.id.fileserver.model.FileInfo;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

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
    public void nonExistingFileError() {
        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke("getFileInfo", Map.of("path", "file1"), FileInfo.class)
        );

        //then
        assertThat(thrown.getCode()).isEqualTo(-32001);
        assertThat(thrown).hasMessageContaining("java.nio.file.NoSuchFileException: file1");
    }

    @Test
    public void fileOutOfRootDirError() {
        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke("getFileInfo", Map.of("path", "/file1"), FileInfo.class)
        );

        //then
        assertThat(thrown.getCode()).isEqualTo(-32001);
        assertThat(thrown).hasMessageContaining("Access forbidden");
    }
}