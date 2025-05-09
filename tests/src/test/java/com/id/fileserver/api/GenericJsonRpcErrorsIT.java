package com.id.fileserver.api;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GenericJsonRpcErrorsIT extends BaseApiIT {

    @Test
    void invalidMethodError() {
        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke("unknownMethod", Map.of())
        );

        //then
        assertThat(thrown.getCode()).isEqualTo(-32601);
        assertThat(thrown).hasMessageContaining("method not found");
    }

    @Test
    void invalidParamError() {
        //when
        JsonRpcClientException thrown = assertThrows(
                JsonRpcClientException.class,
                () -> getClient().invoke("getFileInfo", Map.of("unknownParam", "value"))
        );

        //then
        assertThat(thrown.getCode()).isEqualTo(-32602);
        assertThat(thrown).hasMessageContaining("method parameters invalid");
    }
}