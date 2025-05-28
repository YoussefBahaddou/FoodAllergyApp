package ma.foodallergyai.foodallergyapi.dto;

public class ScanRequest {
    private String userId;
    private String scanType; // "barcode", "name", "ingredients"
    private String scanInput;

    public ScanRequest() {}

    public ScanRequest(String userId, String scanType, String scanInput) {
        this.userId = userId;
        this.scanType = scanType;
        this.scanInput = scanInput;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getScanType() { return scanType; }
    public void setScanType(String scanType) { this.scanType = scanType; }

    public String getScanInput() { return scanInput; }
    public void setScanInput(String scanInput) { this.scanInput = scanInput; }
}