package com.id.fileserver.api;

import com.id.fileserver.model.FileInfo;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test verifies correctness of series of API calls.
 * Just couple of scenarios for demonstration.
 *
 */
public class SequentialOperationsIT extends BaseApiIT {

    @Test
    void treeCreation() throws Throwable {
        //when: create N nested dirs with M files in each dir
        var path = "";
        int dirNum = 10;
        int fileNum = 5;
        for (int i = 0; i < dirNum; i++) {
            path += "dir%d/".formatted(i);
            createDir(path);
            for (int j = 0; j < fileNum; j++) {
                var file = path + "file%d".formatted(j);
                createFile(file);
            }
        }

        //then: deepest file exists
        Path deepestFile = rootPath.resolve(path + "file" + (fileNum - 1));
        assertThat(Files.exists(deepestFile)).isTrue();
    }

    @Test
    void appendToFile() throws Throwable {
        //given
        Path file1 = rootPath.resolve("file1");
        Files.createFile(file1);

        //when: append N lines to the same file sequentially
        var line = "line";
        int lineNum = 100;
        for (int i = 0; i < lineNum; i++) {
            String data = line + i + System.lineSeparator();
            appendToFile("file1", data);
        }

        //then: total line number is correct
        List<String> contents = FileUtils.readLines(file1.toFile(), StandardCharsets.UTF_8);
        assertThat(contents.size()).isEqualTo(lineNum);

        //and: first line and last line is correct
        assertThat(contents.getFirst()).isEqualTo(line + "0");
        assertThat(contents.getLast()).isEqualTo(line + (lineNum - 1));
    }

    private FileInfo createDir(String dirPath) throws Throwable {
        return getClient().invoke("createDirectory", Map.of("path", dirPath), FileInfo.class);
    }

    private FileInfo createFile(String filePath) throws Throwable {
        return getClient().invoke("createFile", Map.of("path", filePath), FileInfo.class);
    }

    private void appendToFile(String filePath, String data) throws Throwable {
        getClient().invoke(
                "appendToFile",
                Map.of("path", filePath, "data", data),
                Void.class);
    }

}