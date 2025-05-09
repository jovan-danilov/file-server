package com.id.fileserver.service;

import com.id.fileserver.model.FileInfo;

import java.io.IOException;
import java.util.List;

public interface FileService {

    FileInfo getFileInfo(String path) throws IOException;

    List<FileInfo> listDirectory(String path) throws IOException;

    FileInfo createFile(String path) throws IOException;

    FileInfo createDirectory(String path) throws IOException;

    void deleteFile(String path) throws IOException;

    void deleteDirectory(String path) throws IOException;

    FileInfo moveFile(String sourcePath, String targetPath) throws IOException;

    FileInfo moveDirectory(String sourcePath, String targetPath) throws IOException;

    FileInfo copyFile(String sourcePath, String targetPath) throws IOException;

    FileInfo copyDirectory(String sourcePath, String targetPath) throws IOException;

    void appendToFile(String path, String data) throws IOException;

    String readFromFile(String path, int offset, int length) throws IOException;

}
