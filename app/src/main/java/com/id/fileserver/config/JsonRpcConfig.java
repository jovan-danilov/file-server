package com.id.fileserver.config;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImplExporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonRpcConfig {

    @Bean
    public static AutoJsonRpcServiceImplExporter autoJsonRpcServiceImplExporter() {
        AutoJsonRpcServiceImplExporter result = new AutoJsonRpcServiceImplExporter();
        //result.setHttpStatusCodeProvider();
        //result.setErrorResolver();
        return result;
    }
}
