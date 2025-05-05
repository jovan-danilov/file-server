package com.id.fileserver.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RootFileConfig {

    @Value("${app.root-directory}")
    private String rootDirectory;

    @Bean
    public Path rootDirPath() {
        return Paths.get(rootDirectory).toAbsolutePath().normalize();
    }

}
