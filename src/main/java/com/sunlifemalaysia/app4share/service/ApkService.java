package com.sunlifemalaysia.app4share.service;

import java.nio.file.Path;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sunlifemalaysia.app4share.exception.InvalidApkFileException;
import com.sunlifemalaysia.app4share.model.ApkFile;

public interface ApkService {

    public ApkFile findByFileUuid(final String fileUuid);

    public List<ApkFile> findAll();

    public void deleteApkFile(final String fileUuid);

    public ApkFile uploadApkFile(final MultipartFile multipartFile) throws InvalidApkFileException;

    public void housekeepOldApkFile(final String appPackage);

    public void uploadApkReleaseNotes(final MultipartFile multipartFile, final String fileUuid);

    public Path getFilePath(final String fileUuid, final String fileName);

    public byte[] getApkIcon(final String fileUuid);

}
