package ma.foodallergyai.foodallergyapi.dto;

import java.util.List;

public class ScanResult {
    private boolean isSafe;
    private List<String> detectedAllergens;
    private String productName;
    private String ingredients;
    private String message;

    public ScanResult() {}

    public ScanResult(boolean isSafe, List<String> detectedAllergens, String productName, String ingredients, String message) {
        this.isSafe = isSafe;
        this.detectedAllergens = detectedAllergens;
        this.productName = productName;
        this.ingredients = ingredients;
        this.message = message;
    }

    public boolean isSafe() { return isSafe; }
    public void setSafe(boolean safe) { isSafe = safe; }

    public void setDetectedAllergens(List<String> detectedAllergens) { this.detectedAllergens = detectedAllergens; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}