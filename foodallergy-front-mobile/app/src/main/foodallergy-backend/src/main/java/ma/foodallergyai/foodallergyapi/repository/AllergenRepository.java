package ma.foodallergyai.foodallergyapi.repository;

import ma.foodallergyai.foodallergyapi.model.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AllergenRepository extends JpaRepository<Allergen, UUID> {
}