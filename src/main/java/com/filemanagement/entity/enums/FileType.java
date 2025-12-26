package com.filemanagement.entity.enums;

public enum FileType {

    IMAGE,
    DOCUMENT,
    SPREADSHEET,
    PDF,
    TEXT,
    ARCHIVE,
    OTHER;

    public static FileType fromFileExtension(String extension) {
        return switch (extension) {
            case "jpg", "jpeg", "png", "gif" -> IMAGE;
            case "doc", "docx" -> DOCUMENT;
            case "xls", "xlsx", "csv" -> SPREADSHEET;
            case "pdf" -> PDF;
            case "txt" -> TEXT;
            case "zip", "rar" -> ARCHIVE;
            default -> OTHER;
        };
    }
}
