package com.id.fileserver.api;

import com.googlecode.jsonrpc4j.spring.rest.JsonRpcRestClient;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

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

    protected JsonRpcRestClient getClient() throws MalformedURLException {
        return new JsonRpcRestClient(new URL(getUrl()));
    }

}