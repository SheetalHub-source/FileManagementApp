package com.filemanagement.entity;

import java.time.LocalDateTime;

import com.filemanagement.entity.enums.FileType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "files")
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", columnDefinition = "TEXT")
    private String fileName;

    @Column(name = "file_path", columnDefinition = "TEXT", length = 200)
    private String filePath;

    @Column(name = "file_type")
    @Enumerated(value = EnumType.STRING)
    private FileType fileType;

    @Column(name = "file_size",comment = "in kb",precision = 1)
    private Long fileSize;

    @CreationTimestamp
    @Column(name = "file_upload_date", updatable = false)
    private LocalDateTime uploadedAt;

}
