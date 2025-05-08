
package com.id.fileserver.endpoint;

import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;
import com.id.fileserver.model.FileInfo;
import java.util.List;
import org.springframework.stereotype.Controller;

@Controller
@JsonRpcService("/jsonrpc/v1/files")
public interface FileApi {

    /**
     * Get information about file/directory
     *
     * @param path relative path
     * @return file info
     * @throws RuntimeException if it doesn't exist
     */
    FileInfo getFileInfo(@JsonRpcParam(value = "path") String path);

    /**
     * Get list of children in directory if it exists. Only direct children are returned
     *
     * @param path relative path
     * @return list of children
     * @throws RuntimeException if it doesn't exist
     */
    List<FileInfo> listDirectory(@JsonRpcParam(value = "path") String path);

    /**
     * Create empty file
     *
     * @param path relative path
     * @return file info of created file
     * @throws RuntimeException if parent directory doesn't exist or error occurred
     */
    FileInfo createFile(@JsonRpcParam(value = "path") String path);

    /**
     * Create empty directory. If path represents subtree full subtree is created
     *
     * @param path relative path
     * @return file info of created directory
     * @throws RuntimeException if error is occurred
     */
    FileInfo createDirectory(@JsonRpcParam(value = "path") String path);

    /**
     * Delete file
     *
     * @param path relative path
     * @throws RuntimeException if it doesn't exist or error occurred
     */
    void deleteFile(@JsonRpcParam(value = "path") String path);

    /**
     * Delete directory. Full subtree is deleted
     *
     * @param path relative path
     * @throws RuntimeException if it doesn't exist or error occurred
     */
    void deleteDirectory(@JsonRpcParam(value = "path") String path);

    /**
     * Move file/directory
     *
     * @param sourcePath relative source path
     * @param targetPath relative target path
     * @return file info of target file
     * @throws RuntimeException if it source doesn't exist or error occurred
     */
    FileInfo moveFile(
            @JsonRpcParam(value = "sourcePath") String sourcePath,
            @JsonRpcParam(value = "targetPath") String targetPath);

    /**
     * Copy file
     *
     * @param sourcePath relative source path
     * @param targetPath relative target path
     * @return file info of target file
     * @throws RuntimeException if source doesn't exist or error occurred
     */
    FileInfo copyFile(
            @JsonRpcParam(value = "sourcePath") String sourcePath,
            @JsonRpcParam(value = "targetPath") String targetPath);

    /**
     * Copy directory
     *
     * @param sourcePath relative source path
     * @param targetPath relative target path
     * @return file info of target file
     * @throws RuntimeException if source doesn't exist or error occurred
     */
    FileInfo copyDirectory(
            @JsonRpcParam(value = "sourcePath") String sourcePath,
            @JsonRpcParam(value = "targetPath") String targetPath);

    /**
     * Append data to a file
     *
     * @param path relative path
     * @param data data to be added
     * @throws RuntimeException if file doesn't exist or error occurred
     */
    void appendToFile(
            @JsonRpcParam(value = "path") String path,
            @JsonRpcParam(value = "data") String data);

    /**
     * Read data to a file
     *
     * @param path   relative path
     * @param offset offset in file in bytes
     * @param length length in bytes
     * @return data
     * @throws RuntimeException if file doesn't exist or error occurred
     */
    String readFromFile(
            @JsonRpcParam(value = "path") String path,
            @JsonRpcParam(value = "offset") int offset,
            @JsonRpcParam(value = "length") int length);
}