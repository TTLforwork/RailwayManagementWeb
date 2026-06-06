package com.railway.dao;

import com.railway.model.Document;
import com.railway.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository; 

@Repository // FIX: ADDED @Repository
public class DocumentDAO {

    public DocumentDAO() {
        createDocumentTablesIfNotExist();
    }

    private void createDocumentTablesIfNotExist() {
        String documentTableSql = "IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Document') " +
                "CREATE TABLE Document (" +
                "DocumentID INT IDENTITY(1,1) PRIMARY KEY, " +
                "DocumentType VARCHAR(50) NOT NULL, " +
                "FileName VARCHAR(255) NOT NULL, " +
                "FilePath VARCHAR(500) NOT NULL, " +
                "FileSize BIGINT, " +
                "UploadedBy VARCHAR(50), " +
                "UploadDate DATETIME DEFAULT GETDATE(), " +
                "Status VARCHAR(20) DEFAULT 'ACTIVE')";

        String passengerDocTableSql = "IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'PassengerDocument') " +
                "CREATE TABLE PassengerDocument (" +
                "PNR VARCHAR(10) NOT NULL, " +
                "DocumentID INT NOT NULL, " +
                "Purpose VARCHAR(100), " +
                "PRIMARY KEY (PNR, DocumentID), " +
                "FOREIGN KEY (PNR) REFERENCES Passenger(PNR) ON DELETE CASCADE, " +
                "FOREIGN KEY (DocumentID) REFERENCES Document(DocumentID) ON DELETE CASCADE)";

        String bookingDocTableSql = "IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'BookingDocument') " +
                "CREATE TABLE BookingDocument (" +
                "PNR VARCHAR(10) NOT NULL, " +
                "DocumentID INT NOT NULL, " +
                "DocumentCategory VARCHAR(20) CHECK (DocumentCategory IN ('TICKET', 'RECEIPT', 'INVOICE')), " +
                "PRIMARY KEY (PNR, DocumentID), " +
                "FOREIGN KEY (PNR) REFERENCES Passenger(PNR) ON DELETE CASCADE, " +
                "FOREIGN KEY (DocumentID) REFERENCES Document(DocumentID) ON DELETE CASCADE)";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(documentTableSql);
            stmt.execute(passengerDocTableSql);
            stmt.execute(bookingDocTableSql);

        } catch (SQLException e) {
            System.err.println("Error creating Document tables: " + e.getMessage());
        }
    }

    public int addDocument(Document document) throws SQLException {
        String sql = "INSERT INTO Document (DocumentType, FileName, FilePath, FileSize, UploadedBy, UploadDate, Status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, document.getDocumentType());
            stmt.setString(2, document.getFileName());
            stmt.setString(3, document.getFilePath());
            stmt.setLong(4, document.getFileSize());
            stmt.setString(5, document.getUploadedBy());
            stmt.setTimestamp(6, Timestamp.valueOf(document.getUploadDate()));
            stmt.setString(7, document.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    public boolean linkPassengerDocument(String pnr, int documentId, String purpose) throws SQLException {
        String sql = "INSERT INTO PassengerDocument (PNR, DocumentID, Purpose) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pnr);
            stmt.setInt(2, documentId);
            stmt.setString(3, purpose);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean linkBookingDocument(String pnr, int documentId, String category) throws SQLException {
        String sql = "INSERT INTO BookingDocument (PNR, DocumentID, DocumentCategory) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pnr);
            stmt.setInt(2, documentId);
            stmt.setString(3, category);

            return stmt.executeUpdate() > 0;
        }
    }

    public List<Document> getDocumentsByPassenger(String pnr) throws SQLException {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT d.*, pd.Purpose FROM Document d " +
                "INNER JOIN PassengerDocument pd ON d.DocumentID = pd.DocumentID " +
                "WHERE pd.PNR = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pnr);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(extractDocumentFromResultSet(rs));
                }
            }
        }
        return documents;
    }

    public List<Document> getDocumentsByBooking(String pnr) throws SQLException {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT d.*, bd.DocumentCategory FROM Document d " +
                "INNER JOIN BookingDocument bd ON d.DocumentID = bd.DocumentID " +
                "WHERE bd.PNR = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pnr);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(extractDocumentFromResultSet(rs));
                }
            }
        }
        return documents;
    }

    public Document getDocumentById(int documentId) throws SQLException {
        String sql = "SELECT * FROM Document WHERE DocumentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractDocumentFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public boolean deleteDocument(int documentId) throws SQLException {
        String sql = "DELETE FROM Document WHERE DocumentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Document extractDocumentFromResultSet(ResultSet rs) throws SQLException {
        Document document = new Document();
        document.setDocumentId(rs.getInt("DocumentID"));
        document.setDocumentType(rs.getString("DocumentType"));
        document.setFileName(rs.getString("FileName"));
        document.setFilePath(rs.getString("FilePath"));
        document.setFileSize(rs.getLong("FileSize"));
        document.setUploadedBy(rs.getString("UploadedBy"));

        Timestamp timestamp = rs.getTimestamp("UploadDate");
        if (timestamp != null) {
            document.setUploadDate(timestamp.toLocalDateTime());
        }

        document.setStatus(rs.getString("Status"));
        return document;
    }
}
