package com.id.fileserver.api;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonRpcRequest {

    private String jsonrpc = "2.0";
    private String id;
    private String method;
    private Map<String, Object> params;

}
