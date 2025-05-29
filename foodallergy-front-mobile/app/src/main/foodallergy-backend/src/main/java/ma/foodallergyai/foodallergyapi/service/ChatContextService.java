package ma.foodallergyai.foodallergyapi.service;

import ma.foodallergyai.foodallergyapi.model.Allergen;
import ma.foodallergyai.foodallergyapi.model.ScanHistory;
import ma.foodallergyai.foodallergyapi.model.User;
import ma.foodallergyai.foodallergyapi.repository.AllergenRepository;
import ma.foodallergyai.foodallergyapi.repository.ScanHistoryRepository;
import ma.foodallergyai.foodallergyapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatContextService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AllergenRepository allergenRepository;

    @Autowired
    private ScanHistoryRepository scanHistoryRepository;

    public String buildUserContext(UUID userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return "User context not available.";
            }

            StringBuilder context = new StringBuilder();
            context.append("User Profile: ").append(user.getUsername()).append(". ");

            // Get user's allergies
            List<Allergen> userAllergies = allergenRepository.findByUserId(userId);
            if (!userAllergies.isEmpty()) {
                String allergiesList = userAllergies.stream()
                        .map(Allergen::getName)
                        .collect(Collectors.joining(", "));
                context.append("User is allergic to: ").append(allergiesList).append(". ");
            } else {
                context.append("User has not set up allergy preferences yet. ");
            }

            // Get recent scan history for context
            List<ScanHistory> recentScans = scanHistoryRepository.findTop5ByUserIdOrderByScannedAtDesc(userId);
            if (!recentScans.isEmpty()) {
                context.append("Recent scans show interest in food safety. ");
                long unsafeScans = recentScans.stream().filter(scan -> !scan.getIsSafe()).count();
                if (unsafeScans > 0) {
                    context.append("User has encountered ").append(unsafeScans).append(" unsafe products recently. ");
                }
            }

            context.append("Provide helpful, accurate advice about food allergies and safety.");

            return context.toString();

        } catch (Exception e) {
            return "User context temporarily unavailable. Provide general food allergy advice.";
        }
    }

    public boolean isRelevantToFoodAllergies(String message) {
        String lowerMessage = message.toLowerCase();

        String[] relevantKeywords = {
            "food", "allergy", "allergic", "ingredient", "safe", "eat", "consume",
            "dairy", "milk", "peanut", "nut", "gluten", "wheat", "egg", "soy",
            "seafood", "fish", "shellfish", "sesame", "scan", "barcode", "label",
            "product", "brand", "restaurant", "cooking", "recipe", "avoid"
        };

        for (String keyword : relevantKeywords) {
            if (lowerMessage.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    public String getTopicRedirectionMessage() {
        return "I'm specifically designed to help with food allergies and food safety. " +
                "I can assist you with:\n\n" +
                "• Checking if foods are safe for your allergies\n" +
                "• Understanding ingredient labels\n" +
                "• Managing your allergy profile\n" +
                "• Scanning products for allergens\n" +
                "• Finding safe alternatives\n\n" +
                "What food allergy question can I help you with?";
    }
}