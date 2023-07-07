package com.sunlifemalaysia.app4share.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunlifemalaysia.app4share.model.IpaFile;

@Repository
public interface IpaFileRepository extends JpaRepository<IpaFile, Long> {
    public Optional<IpaFile> findByFileUuid(String fileUuid);
}
