package com.id.fileserver.api;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericJsonRpcErrorsIT extends BaseApiIT {

    @Test
    public void invalidMethodError() throws Throwable {
        //given: nothing

        //when
        JsonRpcRequest request = JsonRpcRequest.builder()
                .id("1")
                .method("unknownMethod")
                .build();
        JsonRpcErrorResponse result = invokeWithNotFound(request);

        //then
        JsonRpcErrorResponse expected = JsonRpcErrorResponse.builder()
                .jsonrpc("2.0")
                .id("1")
                .error(JsonRpcErrorResponse.Error.builder()
                        .code(-32601)
                        .message("method not found")
                        .build())
                .build();
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    public void invalidParamError() throws Throwable {
        //given: nothing

        //when
        Map<String, Object> params = new HashMap<>();
        params.put("unknownParam", "file1");
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
                        .code(-32602)
                        .message("method parameters invalid")
                        .build())
                .build();
        assertThat(result).isEqualTo(expected);
    }

}