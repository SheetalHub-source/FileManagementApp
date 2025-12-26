package com.filemanagement.service;

import org.apache.tika.mime.MimeTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    ResponseEntity uploadFile(MultipartFile file) throws MimeTypeException, IOException;

    ResponseEntity downloadFile(Long id) throws IOException;

    ResponseEntity deleteFile(Long id) throws IOException;
}
