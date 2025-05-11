package com.id.fileserver.api;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.googlecode.jsonrpc4j.spring.rest.JsonRpcRestClient;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseApiIT {

    private static final String API_PATH = "/jsonrpc/v1/files";

    @Autowired
    Path rootPath;

    @LocalServerPort
    private int port;

    @BeforeEach
    void beforeEach() throws IOException {
        cleanRootDir();
    }

    void cleanRootDir() throws IOException {
        FileUtils.cleanDirectory(rootPath.toFile());
    }

    String getUrl() {
        return "http://localhost:" + port + API_PATH;
    }

    JsonRpcRestClient getClient() throws MalformedURLException, URISyntaxException {
        return new JsonRpcRestClient(new URI(getUrl()).toURL());
    }

    void checkParamIsNull(JsonRpcClientException exception) {
        assertThat(exception.getCode()).isEqualTo(-32098);
        assertThat(exception).hasMessageContaining("Null param");
    }

    void checkParamLength(JsonRpcClientException exception) {
        assertThat(exception.getCode()).isEqualTo(-32098);
        assertThat(exception).hasMessageContaining("Invalid param length");
    }

    void checkFileNotExists(JsonRpcClientException exception) {
        assertThat(exception.getCode()).isEqualTo(-32001);
        assertThat(exception).hasMessageContaining("NoSuchFileException: unknown");
    }

    void checkIsNotFile(JsonRpcClientException exception) {
        assertThat(exception.getCode()).isEqualTo(-32001);
        assertThat(exception).hasMessageContaining("Not a file");
    }

    void checkIsNotDir(JsonRpcClientException exception) {
        assertThat(exception.getCode()).isEqualTo(-32001);
        assertThat(exception).hasMessageContaining("Not a directory");
    }
}