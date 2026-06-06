package com.railway.service;

import com.railway.dao.DocumentDAO;
import com.railway.model.Document;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class DocumentService {
    private final DocumentDAO documentDAO;
    private static final String UPLOAD_DIR = "uploads/";

    public DocumentService() {
        this.documentDAO = new DocumentDAO();
        createUploadDirectoryIfNotExists();
    }

    private void createUploadDirectoryIfNotExists() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    public int uploadDocument(String documentType, String fileName, String username) throws SQLException {
        String filePath = UPLOAD_DIR + System.currentTimeMillis() + "_" + fileName;
        long fileSize = 0;

        Document document = new Document(documentType, fileName, filePath, fileSize, username);
        return documentDAO.addDocument(document);
    }

    public boolean attachDocumentToPassenger(String pnr, int documentId, String purpose) throws SQLException {
        return documentDAO.linkPassengerDocument(pnr, documentId, purpose);
    }

    public boolean attachDocumentToBooking(String pnr, int documentId, String category) throws SQLException {
        if (!category.equals("TICKET") && !category.equals("RECEIPT") && !category.equals("INVOICE")) {
            throw new IllegalArgumentException("Category must be TICKET, RECEIPT, or INVOICE");
        }
        return documentDAO.linkBookingDocument(pnr, documentId, category);
    }

    public List<Document> getPassengerDocuments(String pnr) throws SQLException {
        return documentDAO.getDocumentsByPassenger(pnr);
    }

    public List<Document> getBookingDocuments(String pnr) throws SQLException {
        return documentDAO.getDocumentsByBooking(pnr);
    }

    public Document getDocument(int documentId) throws SQLException {
        return documentDAO.getDocumentById(documentId);
    }

    public boolean deleteDocument(int documentId) throws SQLException {
        Document document = documentDAO.getDocumentById(documentId);
        if (document == null) {
            throw new IllegalArgumentException("Document not found!");
        }

        File file = new File(document.getFilePath());
        if (file.exists()) {
            file.delete();
        }

        return documentDAO.deleteDocument(documentId);
    }

    public String generateTicketDocument(String pnr, String username) throws SQLException {
        String fileName = "ticket_" + pnr + ".pdf";
        int documentId = uploadDocument("TICKET", fileName, username);

        if (documentId > 0) {
            attachDocumentToBooking(pnr, documentId, "TICKET");
            return fileName;
        }
        return null;
    }

    public String generateReceiptDocument(String pnr, String username) throws SQLException {
        String fileName = "receipt_" + pnr + ".pdf";
        int documentId = uploadDocument("RECEIPT", fileName, username);

        if (documentId > 0) {
            attachDocumentToBooking(pnr, documentId, "RECEIPT");
            return fileName;
        }
        return null;
    }
}
