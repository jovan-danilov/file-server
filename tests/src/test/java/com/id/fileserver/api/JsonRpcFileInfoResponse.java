package com.id.fileserver.api;

import com.id.fileserver.model.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonRpcFileInfoResponse {

    private String jsonrpc;
    private String id;
    private FileInfo result;
}
