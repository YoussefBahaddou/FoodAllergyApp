package ma.foodallergyai.foodallergyapi.service;

import ma.foodallergyai.foodallergyapi.dto.ScanRequest;
import ma.foodallergyai.foodallergyapi.dto.ScanResult;
import ma.foodallergyai.foodallergyapi.model.Allergen;
import ma.foodallergyai.foodallergyapi.model.Product;
import ma.foodallergyai.foodallergyapi.model.User;
import ma.foodallergyai.foodallergyapi.repository.ProductRepository;
import ma.foodallergyai.foodallergyapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AllergyService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public ScanResult scanProduct(ScanRequest request) {
        try {
            UUID userId = UUID.fromString(request.getUserId());
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty()) {
                return new ScanResult(false, Collections.emptyList(), "", "", "Utilisateur non trouvé");
            }

            User user = userOpt.get();
            Set<Allergen> userAllergens = user.getAllergens();

            if (userAllergens.isEmpty()) {
                return new ScanResult(false, Collections.emptyList(), "", "", "Aucune allergie configurée");
            }

            String ingredients = "";
            String productName = "";

            switch (request.getScanType().toLowerCase()) {
                case "barcode":
                    Optional<Product> productByBarcode = productRepository.findByBarcode(request.getScanInput());
                    if (productByBarcode.isPresent()) {
                        Product product = productByBarcode.get();
                        ingredients = product.getIngredients();
                        productName = product.getName();
                    } else {
                        return new ScanResult(false, Collections.emptyList(), "", "", "Produit non trouvé dans la base de données");
                    }
                    break;

                case "name":
                    List<Product> productsByName = productRepository.findByNameContainingIgnoreCase(request.getScanInput());
                    if (!productsByName.isEmpty()) {
                        Product product = productsByName.get(0); // Take first match
                        ingredients = product.getIngredients();
                        productName = product.getName();
                    } else {
                        return new ScanResult(false, Collections.emptyList(), "", "", "Produit non trouvé dans la base de données");
                    }
                    break;

                case "ingredients":
                    ingredients = request.getScanInput();
                    productName = "Analyse des ingrédients";
                    break;

                default:
                    return new ScanResult(false, Collections.emptyList(), "", "", "Type de scan non valide");
            }

            // Analyze ingredients for allergens
            List<String> detectedAllergens = analyzeIngredients(ingredients, userAllergens);
            boolean isSafe = detectedAllergens.isEmpty();

            String message = isSafe ?
                "✅ Ce produit semble sûr pour vous !" :
                "⚠️ ATTENTION: Ce produit contient des allergènes dangereux pour vous !";

            return new ScanResult(isSafe, detectedAllergens, productName, ingredients, message);

        } catch (Exception e) {
            return new ScanResult(false, Collections.emptyList(), "", "", "Erreur lors de l'analyse: " + e.getMessage());
        }
    }

    private List<String> analyzeIngredients(String ingredients, Set<Allergen> userAllergens) {
        List<String> detectedAllergens = new ArrayList<>();
        String ingredientsLower = ingredients.toLowerCase();

        for (Allergen allergen : userAllergens) {
            for (String keyword : allergen.getKeywords()) {
                if (ingredientsLower.contains(keyword.toLowerCase())) {
                    detectedAllergens.add(allergen.getName());
                    break; // Don't add the same allergen multiple times
                }
            }
        }

        return detectedAllergens;
    }
}