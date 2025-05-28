package ma.foodallergyai.foodallergyapi.controller;

import ma.foodallergyai.foodallergyapi.dto.ScanRequest;
import ma.foodallergyai.foodallergyapi.dto.ScanResult;
import ma.foodallergyai.foodallergyapi.model.Allergen;
import ma.foodallergyai.foodallergyapi.model.User;
import ma.foodallergyai.foodallergyapi.repository.AllergenRepository;
import ma.foodallergyai.foodallergyapi.repository.UserRepository;
import ma.foodallergyai.foodallergyapi.service.AllergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/allergy")
@CrossOrigin(origins = "*")
public class AllergyController {

    @Autowired
    private AllergyService allergyService;

    @Autowired
    private AllergenRepository allergenRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/scan")
    public ResponseEntity<ScanResult> scanProduct(@RequestBody ScanRequest request) {
        ScanResult result = allergyService.scanProduct(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/allergens")
    public ResponseEntity<List<Allergen>> getAllAllergens() {
        List<Allergen> allergens = allergenRepository.findAll();
        return ResponseEntity.ok(allergens);
    }

    @PostMapping("/user/{userId}/allergens")
    public ResponseEntity<String> updateUserAllergens(@PathVariable String userId, @RequestBody List<String> allergenIds) {
        try {
            UUID userUuid = UUID.fromString(userId);
            Optional<User> userOpt = userRepository.findById(userUuid);

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Utilisateur non trouvé");
            }

            User user = userOpt.get();
            Set<Allergen> allergens = user.getAllergens();
            allergens.clear();

            for (String allergenId : allergenIds) {
                UUID allergenUuid = UUID.fromString(allergenId);
                Optional<Allergen> allergenOpt = allergenRepository.findById(allergenUuid);
                if (allergenOpt.isPresent()) {
                    allergens.add(allergenOpt.get());
                }
            }

            user.setAllergiesSelected(true);
            userRepository.save(user);

            return ResponseEntity.ok("Allergies mises à jour avec succès");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/allergens")
    public ResponseEntity<Set<Allergen>> getUserAllergens(@PathVariable String userId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            Optional<User> userOpt = userRepository.findById(userUuid);

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(userOpt.get().getAllergens());

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}