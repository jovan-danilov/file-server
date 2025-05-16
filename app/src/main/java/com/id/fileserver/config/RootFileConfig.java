package com.id.fileserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

@Configuration
public class RootFileConfig {

    @Value("${app.root-directory}")
    private String rootDirectory;

    @Bean
    public Path rootDirPath() {
        return Paths.get(rootDirectory).toAbsolutePath().normalize();
    }

    @Bean
    public ConcurrentMap<Path, ReentrantLock> fileLocks() {
        return new ConcurrentHashMap<>();
    }

}
