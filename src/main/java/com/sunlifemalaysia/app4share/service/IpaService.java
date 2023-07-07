package com.sunlifemalaysia.app4share.service;

import java.nio.file.Path;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sunlifemalaysia.app4share.exception.InvalidIPAFileException;
import com.sunlifemalaysia.app4share.model.IpaFile;

public interface IpaService {

    public IpaFile findByFileUuid(final String fileUuid);

    public List<IpaFile> findAll();

    public void deleteIpaFile(final String fileUuid);

    public void uploadIpaFile(final MultipartFile multipartFile) throws InvalidIPAFileException;

    public void uploadIpaReleaseNotes(final MultipartFile multipartFile, final String fileUuid);

    public Path getFilePath(final String fileUuid, final String fileName);

    public byte[] getIpaIcon(final String id);

}
