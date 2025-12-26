package com.filemanagement.Repository;

import com.filemanagement.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileMetaDataRepo extends JpaRepository<FileMetadata,Long> {
    Optional<FileMetadata> findByFileName(String originalFilename);
}
