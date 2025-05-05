package com.id.fileserver.api;

import com.id.fileserver.model.FileInfo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FileOperationsApiIT extends BaseApiIT {

    @Test
    public void fileInfo() throws IOException {
        //given
        Path file1 = rootPath.resolve("file1");
        Files.createFile(file1);

        //when
        Map<String, Object> params = new HashMap<>();
        params.put("path", "file1");
        JsonRpcRequest request = JsonRpcRequest.builder()
                .id("1")
                .method("getFileInfo")
                .params(params)
                .build();
        JsonRpcFileInfoResponse body = invokeWithResult(request, JsonRpcFileInfoResponse.class).getBody();

        //then
        assertThat(body).isNotNull();
        FileInfo expected = FileInfo.builder()
                .path("file1")
                .name("file1")
                .size(0)
                .isDirectory(false)
                .build();
        assertThat(body.getResult()).isEqualTo(expected);
    }

    @Test
    public void nonExistingFileError() throws IOException {
        //given: nothing

        //when
        Map<String, Object> params = new HashMap<>();
        params.put("path", "file1");
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
                        .message("java.nio.file.NoSuchFileException: file1")
                        .build())
                .build();
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("error.data")
                .isEqualTo(expected);
    }

}