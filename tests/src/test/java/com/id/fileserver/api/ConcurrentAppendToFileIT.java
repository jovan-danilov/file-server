package com.id.fileserver.api;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * This test verifies correctness of writing ("appendToFile" API) concurrently in several threads.
 * Simulates multi-client access.
 *
 */
public class ConcurrentAppendToFileIT extends BaseApiIT {

    @Test
    void appendConcurrentlyToSeveralFile() throws Throwable {
        //given: 3 files
        Path file1 = rootPath.resolve("concur-file1");
        Path file2 = rootPath.resolve("concur-file2");
        Path file3 = rootPath.resolve("concur-file3");
        Files.createFile(file1);
        Files.createFile(file2);
        Files.createFile(file3);
        int fileNum = 3;

        //when: append concurrently to each file
        boolean finished;
        var line = "a".repeat(50);
        int lineNum = 1_000;
        ExecutorService pool = Executors.newFixedThreadPool(100);
        for (int i = 0; i < lineNum; i++) {
            String data = line + i + System.lineSeparator();

            for (int j = 1; j <= fileNum; j++) {
                int finalJ = j;
                pool.execute(() -> {
                    try {
                        appendToFile("concur-file" + finalJ, data);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        pool.shutdown();
        finished = pool.awaitTermination(30, TimeUnit.SECONDS);

        if (!finished) {
            fail("Test was not completed in time");
        }

        //then
        checkLines(file1, line, lineNum);
        checkLines(file2, line, lineNum);
        checkLines(file3, line, lineNum);
    }

    private void checkLines(Path file1, String line, int lineNum) throws IOException {
        //total line number is correct
        List<String> contents = FileUtils.readLines(file1.toFile(), StandardCharsets.UTF_8);
        assertThat(contents.size()).isEqualTo(lineNum);

        //first line and last line is correct
        Collections.sort(contents);
        assertThat(contents.getFirst()).isEqualTo(line + "0");
        assertThat(contents.getLast()).isEqualTo(line + (lineNum - 1));
    }

    private void appendToFile(String filePath, String data) throws Throwable {
        getClient().invoke(
                "appendToFile",
                Map.of("path", filePath, "data", data),
                Void.class);
    }

}