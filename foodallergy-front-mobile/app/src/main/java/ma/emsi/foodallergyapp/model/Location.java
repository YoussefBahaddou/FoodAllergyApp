package ma.emsi.foodallergyapp.model;

import com.google.android.gms.maps.model.LatLng;

public class Location {
    private String id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String type; // "EMERGENCY" or "RESTAURANT" or "STORE"
    private String description;
    private String phoneNumber;
    private boolean isAllergySafe;
    private String[] allergySafeFeatures;

    public Location() {}

    public Location(String id, String name, String address, double latitude, double longitude, 
                   String type, String description, String phoneNumber, boolean isAllergySafe, 
                   String[] allergySafeFeatures) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.isAllergySafe = isAllergySafe;
        this.allergySafeFeatures = allergySafeFeatures;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public boolean isAllergySafe() { return isAllergySafe; }
    public void setAllergySafe(boolean allergySafe) { isAllergySafe = allergySafe; }

    public String[] getAllergySafeFeatures() { return allergySafeFeatures; }
    public void setAllergySafeFeatures(String[] allergySafeFeatures) { 
        this.allergySafeFeatures = allergySafeFeatures; 
    }
} 