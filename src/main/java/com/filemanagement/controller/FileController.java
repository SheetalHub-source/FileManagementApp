package com.filemanagement.controller;

import com.filemanagement.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) throws MimeTypeException, IOException {
        log.info("Request coming for upload file..");
        return fileService.uploadFile(file);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity downloadFile(@PathVariable Long id) throws IOException {
        log.info("Requested id for file download: {}", id);
        return fileService.downloadFile(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteFileById(@PathVariable Long id) throws IOException {
        log.info("Requested id for delete file: {}", id);
        return fileService.deleteFile(id);
    }
}
