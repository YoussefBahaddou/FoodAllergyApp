package ma.emsi.foodallergyapp.model;

import java.io.Serializable;

public class Allergen implements Serializable {
    private String id;
    private String name;
    private String description;
    private boolean selected;

    public Allergen() {}

    public Allergen(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.selected = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}