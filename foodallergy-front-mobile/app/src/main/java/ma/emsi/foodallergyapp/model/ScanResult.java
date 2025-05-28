package ma.emsi.foodallergyapp.model;

import java.util.List;

public class ScanResult {
    private boolean safe;
    private List<String> detectedAllergens;
    private String productName;
    private String ingredients;
    private String message;

    public ScanResult() {}

    // Getters and setters
    public boolean isSafe() { return safe; }
    public void setSafe(boolean safe) { this.safe = safe; }

    public List<String> getDetectedAllergens() { return detectedAllergens; }
    public void setDetectedAllergens(List<String> detectedAllergens) { this.detectedAllergens = detectedAllergens; }

    public void setProductName(String productName) { this.productName = productName; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}