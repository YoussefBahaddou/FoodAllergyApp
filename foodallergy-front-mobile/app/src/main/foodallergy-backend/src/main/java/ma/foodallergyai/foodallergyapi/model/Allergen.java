package ma.foodallergyai.foodallergyapi.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "allergens")
public class Allergen {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @ElementCollection
    @CollectionTable(name = "allergen_keywords", joinColumns = @JoinColumn(name = "allergen_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    // Constructors, getters, setters
    public Allergen() {}

    public Allergen(String name, String description, List<String> keywords) {
        this.name = name;
        this.description = description;
        this.keywords = keywords;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
}