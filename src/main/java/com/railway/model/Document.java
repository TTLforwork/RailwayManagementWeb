package com.railway.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Document {
    private Integer documentId;
    private String documentType;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String uploadedBy;
    private LocalDateTime uploadDate;
    private String status;

    public Document() {
    }

    public Document(String documentType, String fileName, String filePath,
                    Long fileSize, String uploadedBy) {
        this.documentType = documentType;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.uploadedBy = uploadedBy;
        this.uploadDate = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(documentId, document.documentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId);
    }

    @Override
    public String toString() {
        return "Document{" +
                "documentId=" + documentId +
                ", documentType='" + documentType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", uploadedBy='" + uploadedBy + '\'' +
                ", uploadDate=" + uploadDate +
                '}';
    }
}
