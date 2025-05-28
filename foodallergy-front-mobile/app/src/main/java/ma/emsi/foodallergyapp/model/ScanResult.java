package ma.emsi.foodallergyapp.model;

import java.io.Serializable;
import java.util.List;

public class ScanResult implements Serializable {
    private String productName;
    private String barcode;
    private String brand;
    private String allergenInfo;
    private List<String> ingredients;
    private List<String> allergens;
    private boolean hasUserAllergens;
    private String riskLevel;

    public ScanResult() {}

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
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getAllergenInfo() { return allergenInfo; }
    public void setAllergenInfo(String allergenInfo) { this.allergenInfo = allergenInfo; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getAllergens() { return allergens; }
    public void setAllergens(List<String> allergens) { this.allergens = allergens; }

    public boolean isHasUserAllergens() { return hasUserAllergens; }
    public void setHasUserAllergens(boolean hasUserAllergens) { this.hasUserAllergens = hasUserAllergens; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
}