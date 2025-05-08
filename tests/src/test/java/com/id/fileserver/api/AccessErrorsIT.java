package com.id.fileserver.api;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.id.fileserver.model.FileInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
    public void outOfRootDirectoryAccessError(String methodName) {
        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke(methodName, Map.of("path", "/file"), FileInfo.class)
        );

        //then
        assertThat(thrown.getCode()).isEqualTo(-32099);
        assertThat(thrown).hasMessageContaining("Access forbidden");
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "getFileInfo", "listDirectory",
            "deleteFile", "deleteDirectory"
    })
    public void nonExistError(String methodName) {
        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke(methodName, Map.of("path", "unknown"), FileInfo.class)
        );

        //then
        checkFileNotExists(thrown);
    }
}