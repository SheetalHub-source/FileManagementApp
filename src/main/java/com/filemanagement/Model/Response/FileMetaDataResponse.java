package com.filemanagement.Model.Response;

import com.filemanagement.entity.enums.FileType;

public record FileMetaDataResponse(
        Long id,
        String fileName,
        FileType fileType,
        Long fileSize
) {
}
