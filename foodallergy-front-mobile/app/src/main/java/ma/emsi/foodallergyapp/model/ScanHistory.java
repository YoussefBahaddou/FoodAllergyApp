package ma.emsi.foodallergyapp.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ScanHistory {
    private UUID id;
    private UUID userId;
    private UUID productId;
    private String scanType;
    private String scanInput;
    private boolean isSafe;
    private List<String> detectedAllergens;
    private Date scannedAt;
    private String productName;
    private String productBrand;
    private String status;
    private String timeAgo;

    // Default constructor
    public ScanHistory() {
        this.scannedAt = new Date();
        this.status = "Unknown";
        this.timeAgo = "Just now";
    }

    // Constructor with essential fields
    public ScanHistory(UUID userId, String scanType, String scanInput, boolean isSafe) {
        this.userId = userId;
        this.scanType = scanType;
        this.scanInput = scanInput;
        this.isSafe = isSafe;
        this.scannedAt = new Date();
        this.status = isSafe ? "Safe" : "Unsafe";
        this.timeAgo = "Just now";
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public String getScanInput() {
        return scanInput;
    }

    public void setScanInput(String scanInput) {
        this.scanInput = scanInput;
    }

    public boolean isSafe() {
        return isSafe;
    }

    public void setSafe(boolean safe) {
        isSafe = safe;
        this.status = isSafe ? "Safe" : "Unsafe";
    }

    public List<String> getDetectedAllergens() {
        return detectedAllergens;
    }

    public void setDetectedAllergens(List<String> detectedAllergens) {
        this.detectedAllergens = detectedAllergens;
    }

    public Date getScannedAt() {
        return scannedAt;
    }

    public void setScannedAt(Date scannedAt) {
        this.scannedAt = scannedAt;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getStatus() {
        return status != null ? status : (isSafe ? "Safe" : "Unsafe");
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeAgo() {
        return timeAgo != null ? timeAgo : "Just now";
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    @Override
    public String toString() {
        return "ScanHistory{" +
                "id=" + id +
                ", userId=" + userId +
                ", productId=" + productId +
                ", scanType='" + scanType + '\'' +
                ", scanInput='" + scanInput + '\'' +
                ", isSafe=" + isSafe +
                ", detectedAllergens=" + detectedAllergens +
                ", scannedAt=" + scannedAt +
                ", productName='" + productName + '\'' +
                ", productBrand='" + productBrand + '\'' +
                ", status='" + status + '\'' +
                ", timeAgo='" + timeAgo + '\'' +
                '}';
    }
}