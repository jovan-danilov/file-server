package com.id.fileserver.api;

import com.id.fileserver.model.FileInfo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectoryOperationsApiIT extends BaseApiIT {

    @Test
    void dirInfo() throws IOException {
        //given
        Path dir1 = rootPath.resolve("dir1");
        Files.createDirectories(dir1);

        //when
        Map<String, Object> params = new HashMap<>();
        params.put("path", "dir1");
        JsonRpcRequest request = JsonRpcRequest.builder()
                .id("1")
                .method("getFileInfo")
                .params(params)
                .build();
        JsonRpcFileInfoResponse body = invokeWithResult(request, JsonRpcFileInfoResponse.class).getBody();

        //then
        assertThat(body).isNotNull();
        FileInfo expected = FileInfo.builder()
                .name("dir1")
                .path("dir1")
                .size(0)
                .isDirectory(true)
                .build();
        assertThat(body.getResult()).isEqualTo(expected);
    }

    @Test
    public void nonExistingDirError() throws IOException {
        //given: nothing

        //when
        Map<String, Object> params = new HashMap<>();
        params.put("path", "dir1");
        JsonRpcRequest request = JsonRpcRequest.builder()
                .id("1")
                .method("getFileInfo")
                .params(params)
                .build();
        JsonRpcErrorResponse result = invokeWithServerError(request);

        //then
        JsonRpcErrorResponse expected = JsonRpcErrorResponse.builder()
                .jsonrpc("2.0")
                .id("1")
                .error(JsonRpcErrorResponse.Error.builder()
                        .code(-32001)
                        .message("java.nio.file.NoSuchFileException: dir1")
                        .build())
                .build();
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("error.data")
                .isEqualTo(expected);
    }

}
