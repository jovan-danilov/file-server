package com.id.fileserver.service;

import com.id.fileserver.model.FileInfo;
import java.io.IOException;
import java.util.List;

public interface FileService {

    FileInfo getFileInfo(String path) throws IOException;

    List<FileInfo> listDirectory(String path) throws IOException;

    FileInfo createFile(String path) throws IOException;

    FileInfo createDirectory(String path) throws IOException;

    boolean deleteFile(String path) throws IOException;

    boolean deleteDirectory(String path) throws IOException;

    FileInfo moveFile(String sourcePath, String targetPath) throws IOException;

    FileInfo copyFile(String sourcePath, String targetPath) throws IOException;

    boolean appendData(String path, String data) throws IOException;

    String readData(String path, int offset, int length) throws IOException;

}
