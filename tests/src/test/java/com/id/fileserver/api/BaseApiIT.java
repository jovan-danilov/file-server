package com.id.fileserver.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseApiIT {

    private static final String API_PATH = "/jsonrpc/v1/files";

    @Autowired
    Path rootPath;

    @LocalServerPort
    private int port;

    @Autowired
    ObjectMapper mapper;

    RestTemplate restTemplate;

    public BaseApiIT() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(new MediaType("application", "json-rpc")));
        messageConverters.add(converter);
        restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(messageConverters);
    }

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

    <T> ResponseEntity<T> invokeWithResult(JsonRpcRequest request, Class<T> clazz) {
        ResponseEntity<T> result = restTemplate.exchange(getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(request),
                clazz);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        return result;
    }

    JsonRpcErrorResponse invokeWithNotFound(JsonRpcRequest request) throws JsonProcessingException {
        JsonRpcErrorResponse result = null;
        try {
            restTemplate.exchange(getUrl(), HttpMethod.POST,
                    new HttpEntity<>(request), JsonRpcErrorResponse.class);
        }
        catch (HttpClientErrorException.NotFound e) {
            String responseBody = e.getResponseBodyAsString();
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            result = mapper.readValue(responseBody, JsonRpcErrorResponse.class);
        }
        return result;
    }

    JsonRpcErrorResponse invokeWithServerError(JsonRpcRequest request)
            throws JsonProcessingException {
        JsonRpcErrorResponse result = null;
        try {
            restTemplate.exchange(getUrl(), HttpMethod.POST,
                    new HttpEntity<>(request), JsonRpcErrorResponse.class);
        }
        catch (HttpServerErrorException.InternalServerError e) {
            String responseBody = e.getResponseBodyAsString();
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            result = mapper.readValue(responseBody, JsonRpcErrorResponse.class);
        }
        return result;
    }

}