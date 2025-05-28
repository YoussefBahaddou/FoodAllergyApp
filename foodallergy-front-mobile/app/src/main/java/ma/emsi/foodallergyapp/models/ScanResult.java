package ma.emsi.foodallergyapp.models;

import java.util.List;

public class ScanResult {
    private String productName;
    private String barcode;
    private List<String> ingredients;
    private List<String> allergens;
    private boolean hasUserAllergens;
    private String riskLevel; // LOW, MEDIUM, HIGH
    private String imageUrl;

    public ScanResult() {
    }

    public ScanResult(String productName, String barcode, List<String> ingredients,
                     List<String> allergens, boolean hasUserAllergens, String riskLevel) {
        this.productName = productName;
        this.barcode = barcode;
        this.ingredients = ingredients;
        this.allergens = allergens;
        this.hasUserAllergens = hasUserAllergens;
        this.riskLevel = riskLevel;
    }

    // Getters and setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<String> allergens) {
        this.allergens = allergens;
    }

    public boolean isHasUserAllergens() {
        return hasUserAllergens;
    }

    public void setHasUserAllergens(boolean hasUserAllergens) {
        this.hasUserAllergens = hasUserAllergens;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}