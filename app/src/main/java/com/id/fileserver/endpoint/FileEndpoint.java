
package com.id.fileserver.endpoint;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import com.id.fileserver.model.FileInfo;
import com.id.fileserver.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@AutoJsonRpcServiceImpl
@RequiredArgsConstructor
public class FileEndpoint implements FileApi {

    private final FileService fileService;

    public FileInfo getFileInfo(String path) {
        log.info("getFileInfo: {}", path);
        try {
            return fileService.getFileInfo(path);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<FileInfo> listDirectory(String path) {
        log.info("listDirectory: {}", path);
        try {
            return fileService.listDirectory(path);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileInfo createFile(String path) {
        log.info("createFile: {}", path);
        try {
            return fileService.createFile(path);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileInfo createDirectory(String path) {
        log.info("createDirectory: {}", path);
        try {
            return fileService.createDirectory(path);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String path) {
        log.info("deleteFile: {}", path);
        try {
            fileService.deleteFile(path);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteDirectory(String path) {
        log.info("deleteDirectory: {}", path);
        try {
            fileService.deleteDirectory(path);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileInfo copyFile(String sourcePath, String targetPath) {
        log.info("copyFile, source: {}, target : {}", sourcePath, targetPath);
        try {
            return fileService.copyFile(sourcePath, targetPath);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileInfo copyDirectory(String sourcePath, String targetPath) {
        log.info("copyDirectory, source: {}, target : {}", sourcePath, targetPath);
        try {
            return fileService.copyDirectory(sourcePath, targetPath);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileInfo moveFile(String sourcePath, String targetPath) {
        log.info("moveFile, source: {}, target : {}", sourcePath, targetPath);
        try {
            return fileService.moveFile(sourcePath, targetPath);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileInfo moveDirectory(String sourcePath, String targetPath) {
        log.info("moveDirectory, source: {}, target : {}", sourcePath, targetPath);
        try {
            return fileService.moveDirectory(sourcePath, targetPath);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void appendToFile(String path, String data) {
        log.info("appendToFile: {}, data: {} ", path, data.length());
        try {
            fileService.appendToFile(path, data);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readFromFile(String path, int offset, int length) {
        log.info("readFromFile: {}, offset: {}, length: {}", path, offset,length);
        try {
            return fileService.readFromFile(path, offset, length);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}