package com.sunlifemalaysia.app4share.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    static final Logger logger = LoggerFactory.getLogger(FileService.class);
    static final String ERROR_FORMAT = "{} {} {}";

    default File transferMultipartFileTo(final MultipartFile multipartFile, final Path toPath) {
        try {
            logger.info("transferring to {}", toPath);
            Files.createDirectories(toPath.getParent());
            multipartFile.transferTo(toPath);
            return toPath.toFile();

        } catch (IOException ex) {
            logger.error(ERROR_FORMAT, ex.getStackTrace()[0], ex.getClass(), ex.getMessage());
        }

        return null;
    }

    default File moveFileTo(Path fromPath, Path toPath) {
        try {
            logger.info("moving {} to {}", fromPath, toPath);
            Files.createDirectories(toPath.getParent());
            return Files.move(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING).toFile();
        } catch (IOException ex) {
            logger.error(ERROR_FORMAT, ex.getStackTrace()[0], ex.getClass(), ex.getMessage());
        }

        return null;
    }

    default File writeToFile(byte[] bytes, Path toPath) {
        try {
            logger.info("writing bytes to {}", toPath);
            Files.createDirectories(toPath.getParent());
            return Files.write(toPath, bytes).toFile();
        } catch (IOException ex) {
            logger.error(ERROR_FORMAT, ex.getStackTrace()[0], ex.getClass(), ex.getMessage());
        }

        return null;
    }

    default boolean deleteFile(File file) {

        return deleteFile(file.toPath());

    }

    default boolean deleteFile(Path path) {

        try {
            logger.info("deleting {}", path);
            return Files.deleteIfExists(path);
        } catch (IOException ex) {
            logger.error(ERROR_FORMAT, ex.getStackTrace()[0], ex.getClass(), ex.getMessage());

        }

        return false;
    }

    default boolean deleteAllFiles(Path path) {

        File file = path.toFile();

        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteAllFiles(child.toPath());
            }
        }

        return deleteFile(file);
    }

}
