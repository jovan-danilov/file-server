package com.id.fileserver;

import com.id.fileserver.config.EnvProperty;
import com.id.fileserver.config.RootFileConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class FileServerApp {

    private final RootFileConfig rootFileConfig;

    public static void main(String[] args) {
        log.info("JAVA_MIN_MEM: {}", System.getenv("JAVA_MIN_MEM"));
        log.info("JAVA_MAX_MEM: {}", System.getenv("JAVA_MAX_MEM"));
        log.info("Environment vars: " + EnvProperty.getEnvProperties());
        SpringApplication app = new SpringApplication(FileServerApp.class);
        app.setLogStartupInfo(true);
        app.run(args);
        log.info("Application started");
    }

    @PostConstruct
    void onPostConstruct() {
        Path rootDirectoryPath = rootFileConfig.rootDirPath();
        log.info("Root directory path: {}", rootDirectoryPath);
        if (!Files.exists(rootDirectoryPath)) {
            try {
                Files.createDirectories(rootDirectoryPath);
            }
            catch (IOException e) {
                log.error("Failed to create root directory: {}", rootDirectoryPath.toAbsolutePath(), e);
                System.exit(1);
            }
        }
    }

}