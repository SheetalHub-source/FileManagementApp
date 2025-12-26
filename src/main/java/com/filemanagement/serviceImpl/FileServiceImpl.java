package com.filemanagement.serviceImpl;

import com.filemanagement.Exception.FileNotFoundException;
import com.filemanagement.Exception.StorageException;
import com.filemanagement.Exception.UnsupportedFileTypeException;
import com.filemanagement.Model.Response.FileMetaDataResponse;
import com.filemanagement.Model.Response.ResponseModel;
import com.filemanagement.Repository.FileMetaDataRepo;
import com.filemanagement.constant.AppConstant;
import com.filemanagement.entity.FileMetadata;
import com.filemanagement.entity.enums.FileType;
import com.filemanagement.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload.dir}")
    private String pathDir;
    private final FileMetaDataRepo metaDataRepo;
    private final ObjectMapper mapper;

    public FileServiceImpl(FileMetaDataRepo metaDataRepo, ObjectMapper mapper) {
        this.metaDataRepo = metaDataRepo;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity uploadFile(MultipartFile multipartFile) throws MimeTypeException, IOException {
        log.info("File uploading process start..");

        if (multipartFile.isEmpty()) {
            throw new FileNotFoundException("Empty file can't be uploaded");
        }
        if (!isValidExtension(multipartFile)) {
            throw new UnsupportedFileTypeException("Client sent a file, but the server does not support that media type.");
        }
        Path uploadDir = Paths.get(pathDir);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String originalFilename = multipartFile.getOriginalFilename();
        Path filePath = Paths.get(pathDir, originalFilename);

        if (!filePath.normalize().startsWith(Paths.get(pathDir).normalize())) {
            throw new StorageException("Cannot store file outside the current directory.");
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + originalFilename);
        }
        FileMetadata metadata = metaDataRepo.findByFileName(originalFilename).orElse(FileMetadata.builder().fileName(originalFilename).build());

        metadata.setFilePath(filePath.toAbsolutePath().toString());
        metadata.setFileType(FileType.fromFileExtension(originalFilename.substring(originalFilename.lastIndexOf('.') + 1)));
        metadata.setFileSize((long) (multipartFile.getSize() / 1024.0));

        metadata = metaDataRepo.save(metadata);

        FileMetaDataResponse fileMetaDataResponse = mapper.convertValue(metadata, FileMetaDataResponse.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel<>(fileMetaDataResponse, "File uploaded successfully", "success", HttpStatus.OK.value()));
    }


    public Boolean isValidExtension(MultipartFile file) throws MimeTypeException {
        MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
        MimeType mimeType = allTypes.forName(file.getContentType());
        String extension = mimeType.getExtension();
        String substring = extension.substring(1);
        return AppConstant.extensions.contains(substring) ? true : false;
    }

    @Override
    public ResponseEntity downloadFile(Long id) throws IOException {
        FileMetadata metadata = metaDataRepo.findById(id).orElseThrow(() -> new FileNotFoundException("File not found with ID: " + id));

        Path filePath = Paths.get(metadata.getFilePath());
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found on server: " + metadata.getFileName());
        }
        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) contentType = "application/octet-stream";
        log.info("File downloaded successfully.");
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
    }

    @Override
    public ResponseEntity<?> deleteFile(Long id) throws IOException {
        FileMetadata metadata = metaDataRepo.findById(id).orElseThrow(() -> new FileNotFoundException("File not found with ID: " + id));

        Path filePath = Paths.get(metadata.getFilePath());

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        } else {
            throw new FileNotFoundException("File not found on server: " + metadata.getFileName());
        }
        metaDataRepo.deleteById(id);

        log.info("File deleted Successfully for id {}", id);
        return ResponseEntity.ok(new ResponseModel<>(null, "File deleted successfully", "success", HttpStatus.OK.value()));
    }

}
