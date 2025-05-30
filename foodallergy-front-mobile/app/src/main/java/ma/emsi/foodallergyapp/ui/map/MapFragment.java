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

        return view;
    }

    private void setupFilterChips(View view) {
        ChipGroup chipGroup = view.findViewById(R.id.filter_chip_group);
        chipGroup.setOnCheckedChangeListener((group, checkedIds) -> {
            updateMapMarkers();
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
        // TODO: Load locations from your backend
        // For now, we'll add some sample locations
        addSampleLocations();
    }

    private void addSampleLocations() {
        // Add sample emergency locations
        locations.add(new Location(
                "1",
                "Emergency Medical Center",
                "123 Main St",
                33.5731, -7.5898,
                "EMERGENCY",
                "24/7 Emergency Medical Services",
                "+212-123-456-789",
                false,
                null
        ));

        // Add sample allergy-safe restaurant
        locations.add(new Location(
                "2",
                "Allergy-Safe Restaurant",
                "456 Food Ave",
                33.5732, -7.5899,
                "RESTAURANT",
                "Dedicated allergy-safe kitchen",
                "+212-987-654-321",
                true,
                new String[]{"Gluten-free options", "Nut-free kitchen", "Dairy-free menu"}
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