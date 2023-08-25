package com.sunlifemalaysia.app4share.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunlifemalaysia.app4share.model.ApkFile;

public interface ApkFileRepository extends JpaRepository<ApkFile, Long> {
    public Optional<ApkFile> findByFileUuid(String fileUuid);

    public List<ApkFile> findAllByAppPackageOrderByIdDesc(String appPackage);
}
