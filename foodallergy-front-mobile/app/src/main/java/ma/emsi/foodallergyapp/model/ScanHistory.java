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

    // Default constructor
    public ScanHistory() {}

    // Constructor with essential fields
    public ScanHistory(UUID userId, String scanType, String scanInput, boolean isSafe) {
        this.userId = userId;
        this.scanType = scanType;
        this.scanInput = scanInput;
        this.isSafe = isSafe;
        this.scannedAt = new Date();
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
                '}';
    }
}