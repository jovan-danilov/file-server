package com.id.fileserver.api;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.id.fileserver.model.FileInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccessErrorsIT extends BaseApiIT {

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "getFileInfo", "listDirectory",
            "createFile", "createDirectory",
            "deleteFile", "deleteDirectory"
    })
    void pathOutOfRootDirectoryAccessErrorGivenRootPath(String methodName) {
        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke(methodName, Map.of("path", "/path"), FileInfo.class)
        );

        //then
        checkAccessForbidden(thrown);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "getFileInfo", "listDirectory",
            "createFile", "createDirectory",
            "deleteFile", "deleteDirectory"
    })
    void pathOutOfRootDirectoryAccessErrorGivenPlaceholders(String methodName) {
        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke(methodName, Map.of("path", "../../../"), FileInfo.class)
        );

        //then
        checkAccessForbidden(thrown);

        //and
        thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke(methodName, Map.of("path", "../.."), FileInfo.class)
        );

        //then
        checkAccessForbidden(thrown);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "copyFile", "copyDirectory",
            "moveFile", "moveDirectory"
    })
    void sourcePathOutOfRootDirectoryAccessError(String methodName) {
        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke(
                        methodName,
                        Map.of("sourcePath", "/source", "targetPath", "target"),
                        FileInfo.class)
        );

        //then
        checkAccessForbidden(thrown);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "copyFile", "moveFile"
    })
    void targetOutOfRootDirectoryAccessError1(String methodName) throws IOException {
        //given
        Path file = rootPath.resolve("source");
        Files.createFile(file);

        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke(
                        methodName,
                        Map.of("sourcePath", "source", "targetPath", "/target"),
                        FileInfo.class)
        );

        //then
        checkAccessForbidden(thrown);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "copyDirectory", "moveDirectory"
    })
    void targetOutOfRootDirectoryAccessError2(String methodName) throws IOException {
        //given
        Path dir = rootPath.resolve("source");
        Files.createDirectories(dir);

        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke(
                        methodName,
                        Map.of("sourcePath", "source", "targetPath", "/target"),
                        FileInfo.class)
        );

        //then
        checkAccessForbidden(thrown);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "getFileInfo", "listDirectory",
            "deleteFile", "deleteDirectory"
    })
    void pathNotExistsError(String methodName) {
        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke(methodName, Map.of("path", "unknown"), FileInfo.class)
        );

        //then
        checkFileNotExists(thrown);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "copyFile", "copyDirectory",
            "moveFile", "moveDirectory"
    })
    void sourcePathNotExistsError(String methodName) {
        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke(
                        methodName,
                        Map.of("sourcePath", "unknown", "targetPath", "unknown"),
                        FileInfo.class)
        );

        //then
        checkFileNotExists(thrown);
    }

    @Test
    void createFileNotExistingParentError() throws Throwable {
        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke("createFile", Map.of("path", "unknown/file1"), FileInfo.class)
        );

        //then
        checkFileNotExists(thrown);
    }

    private void checkAccessForbidden(JsonRpcClientException thrown) {
        assertThat(thrown.getCode()).isEqualTo(-32099);
        assertThat(thrown).hasMessageContaining("Access forbidden");
    }

}