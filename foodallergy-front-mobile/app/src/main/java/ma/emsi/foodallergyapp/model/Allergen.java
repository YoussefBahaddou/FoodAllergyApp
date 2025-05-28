package ma.emsi.foodallergyapp.model;

public class Allergen {
    private String id;
    private String name;
    private String description;
    private String[] keywords;

    public Allergen() {
    }

    public Allergen(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Allergen(String id, String name, String description, String[] keywords) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.keywords = keywords;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getKeywords() {
        return keywords;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Allergen allergen = (Allergen) obj;
        return id != null ? id.equals(allergen.id) : allergen.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Allergen{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
