package ma.emsi.foodallergyapp.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ma.emsi.foodallergyapp.R;
import ma.emsi.foodallergyapp.model.Location;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private List<Location> locations = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();
    private MaterialButton btnScan;
    private boolean isScanning = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Setup filter chips
        setupFilterChips(view);

        // Setup location button
        setupLocationButton(view);

        // Setup scan button
        setupScanButton(view);

        return view;
    }

    private void setupFilterChips(View view) {
        ChipGroup chipGroup = view.findViewById(R.id.filter_chip_group);
        btnScan = view.findViewById(R.id.btn_scan);
        
        // Set initial visibility
        btnScan.setVisibility(View.GONE);
        
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Check if the scan nearby chip is selected
            boolean scanNearbySelected = (checkedId == R.id.chip_scan_nearby);
            
            // Show/hide scan button based on chip selection
            btnScan.setVisibility(scanNearbySelected ? View.VISIBLE : View.GONE);
            
            // Only update markers if scan nearby is not selected
            if (!scanNearbySelected) {
                updateMapMarkers();
            }
        });
    }

    private void setupLocationButton(View view) {
        FloatingActionButton fab = view.findViewById(R.id.fab_my_location);
        fab.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                getCurrentLocation();
            } else {
                requestLocationPermission();
            }
        });
    }

    private void setupScanButton(View view) {
        btnScan = view.findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                startScanning();
            } else {
                requestLocationPermission();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        
        // Enable zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        // Check location permission
        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
        } else {
            requestLocationPermission();
        }

        // Load locations
        loadLocations();

        // Set default camera position to Casablanca
        LatLng casablanca = new LatLng(33.5731, -7.5898);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casablanca, 12));

        // Set marker click listener
        mMap.setOnMarkerClickListener(marker -> {
            Location location = (Location) marker.getTag();
            if (location != null) {
                showLocationDetails(location);
            }
            return true;
        });
    }

    private void showLocationDetails(Location location) {
        // Create and show a dialog with location details
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_location_details, null);
        
        // Set location details
        ((android.widget.TextView) dialogView.findViewById(R.id.tv_location_name)).setText(location.getName());
        ((android.widget.TextView) dialogView.findViewById(R.id.tv_location_address)).setText(location.getAddress());
        ((android.widget.TextView) dialogView.findViewById(R.id.tv_location_description)).setText(location.getDescription());
        ((android.widget.TextView) dialogView.findViewById(R.id.tv_location_phone)).setText(location.getPhoneNumber());

        // Setup call button
        dialogView.findViewById(R.id.btn_call).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + location.getPhoneNumber()));
            startActivity(intent);
        });

        // Setup directions button
        dialogView.findViewById(R.id.btn_directions).setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location.getLatitude() + "," + location.getLongitude());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        builder.setView(dialogView);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(
                                    location.getLatitude(),
                                    location.getLongitude()
                            );
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    currentLocation, 15));
                        }
                    });
        }
    }

    private void loadLocations() {
        // Add Moroccan emergency locations
        locations.add(new Location(
                "1",
                "Hôpital Ibn Rochd",
                "Avenue Hassan II, Casablanca",
                33.5731, -7.5898,
                "EMERGENCY",
                "Centre hospitalier universitaire avec service d'urgence 24/7",
                "+212-522-482-000",
                false,
                null
        ));

        locations.add(new Location(
                "2",
                "Clinique Al Shifa",
                "Boulevard Anfa, Casablanca",
                33.5732, -7.5899,
                "EMERGENCY",
                "Clinique privée avec service d'urgence",
                "+212-522-482-111",
                false,
                null
        ));

        // Add Moroccan allergy-safe restaurants
        locations.add(new Location(
                "3",
                "Restaurant Le Petit Chef",
                "Rue Mohammed V, Casablanca",
                33.5733, -7.5900,
                "RESTAURANT",
                "Restaurant avec options sans allergènes",
                "+212-522-482-222",
                true,
                new String[]{"Options sans gluten", "Cuisine sans noix", "Menu sans produits laitiers"}
        ));

        locations.add(new Location(
                "4",
                "La Table du Maroc",
                "Avenue Hassan I, Casablanca",
                33.5734, -7.5901,
                "RESTAURANT",
                "Restaurant traditionnel marocain avec options sans allergènes",
                "+212-522-482-333",
                true,
                new String[]{"Options sans gluten", "Cuisine sans noix", "Menu végétarien"}
        ));

        // Add Moroccan allergy-safe stores
        locations.add(new Location(
                "5",
                "Bio Market",
                "Boulevard Zerktouni, Casablanca",
                33.5735, -7.5902,
                "STORE",
                "Magasin bio avec section sans allergènes",
                "+212-522-482-444",
                true,
                new String[]{"Produits sans gluten", "Produits sans noix", "Produits sans lactose"}
        ));

        locations.add(new Location(
                "6",
                "Naturalia",
                "Rue Tariq Ibn Ziad, Casablanca",
                33.5736, -7.5903,
                "STORE",
                "Magasin de produits naturels et bio",
                "+212-522-482-555",
                true,
                new String[]{"Produits bio", "Section sans allergènes", "Produits locaux"}
        ));

        updateMapMarkers();
    }

    private void updateMapMarkers() {
        // Clear existing markers
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();

        // Get selected filters
        boolean showEmergency = isChipSelected(R.id.chip_emergency);
        boolean showRestaurants = isChipSelected(R.id.chip_restaurants);
        boolean showStores = isChipSelected(R.id.chip_stores);

        // If no filters are selected, show all markers
        if (!showEmergency && !showRestaurants && !showStores) {
            showEmergency = true;
            showRestaurants = true;
            showStores = true;
        }

        // Add markers for filtered locations
        for (Location location : locations) {
            if ((location.getType().equals("EMERGENCY") && showEmergency) ||
                (location.getType().equals("RESTAURANT") && showRestaurants) ||
                (location.getType().equals("STORE") && showStores)) {
                
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(location.getLatLng())
                        .title(location.getName())
                        .snippet(location.getAddress());

                // Set different marker colors based on type
                if (location.getType().equals("EMERGENCY")) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_RED));
                } else if (location.isAllergySafe()) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN));
                }

                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(location);
                markers.add(marker);
            }
        }
    }

    private boolean isChipSelected(int chipId) {
        Chip chip = requireView().findViewById(chipId);
        return chip != null && chip.isChecked();
    }

    private void startScanning() {
        if (isScanning) return;
        
        isScanning = true;
        btnScan.setEnabled(false);
        Toast.makeText(requireContext(), R.string.scanning_locations, Toast.LENGTH_SHORT).show();

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(
                                    location.getLatitude(),
                                    location.getLongitude()
                            );
                            // Move camera to current location
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    currentLocation, 15));
                            
                            // Clear existing markers
                            clearMarkers();
                            
                            // Add nearby locations
                            addNearbyLocations(currentLocation);
                            
                            // Show completion message
                            Toast.makeText(requireContext(), R.string.scan_complete, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), 
                                R.string.error_loading_locations, 
                                Toast.LENGTH_SHORT).show();
                        }
                        isScanning = false;
                        btnScan.setEnabled(true);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), 
                            R.string.error_loading_locations, 
                            Toast.LENGTH_SHORT).show();
                        isScanning = false;
                        btnScan.setEnabled(true);
                    });
        }
    }

    private void addNearbyLocations(LatLng center) {
        // In a real app, this would make API calls to get nearby places
        // For now, we'll add some sample locations around the current position
        
        // Add a hospital
        locations.add(new Location(
                "nearby1",
                "Hôpital Proche",
                "À proximité de votre position",
                center.latitude + 0.002,
                center.longitude + 0.002,
                "EMERGENCY",
                "Service d'urgence disponible 24/7",
                "+212-522-000-001",
                false,
                null
        ));

        // Add a store
        locations.add(new Location(
                "nearby2",
                "Magasin Bio Proche",
                "À proximité de votre position",
                center.latitude - 0.002,
                center.longitude - 0.002,
                "STORE",
                "Section produits sans allergènes",
                "+212-522-000-002",
                true,
                new String[]{"Produits sans gluten", "Produits sans noix"}
        ));

        updateMapMarkers();
    }

    private void clearMarkers() {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
        locations.clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null) {
                    mMap.setMyLocationEnabled(true);
                    getCurrentLocation();
                }
            } else {
                Toast.makeText(requireContext(),
                        R.string.location_permission_denied,
                        Toast.LENGTH_LONG).show();
            }
        }
    }
} 