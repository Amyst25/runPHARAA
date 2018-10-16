package ch.epfl.sweng.runpharaa;

import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Arrays;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;

public class CreateTrackActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private TextView totalDistanceText, totalAltitudeText;
    private EditText nameText;
    private double minAltitude = Double.POSITIVE_INFINITY;
    private double maxAltitude = Double.NEGATIVE_INFINITY;
    private Location[] locations;
    private LatLng[] points;
    private double totalDistance = 0.0, totalAltitudeChange = 0.0;
    private Button createButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_track_2);
        totalDistanceText = findViewById(R.id.create_text_total_distance);
        totalAltitudeText = findViewById(R.id.create_text_total_altitude);
        nameText = findViewById(R.id.create_text_name);
        createButton = findViewById(R.id.create_track_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create track
                Track t = new Track(nameText.getText().toString(), points);
                // TODO: add track to created tracks
                Toast.makeText(getApplicationContext(), "New track was successfully created !", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        // Get map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.create_map_view);
        mapFragment.getMapAsync(this);
    }

    /**
     * Extracts information from the bundle
     */
    private void handleExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Parcelable[] a = bundle.getParcelableArray("locations");
            locations = Arrays.copyOf(a, a.length, Location[].class);
            a = bundle.getParcelableArray("points");
            points = Arrays.copyOf(a, a.length, LatLng[].class);

            computeValues();

            // Show extracted info
            totalDistanceText.setText(String.format("Total distance: %.2f m", totalDistance));
            totalAltitudeText.setText(String.format("Total altitude difference: %.2f m", totalAltitudeChange));
        }
    }

    /**
     * Computes the total distance and total altitude difference of the track
     */
    private void computeValues() {
        // TODO: will we store this info somewhere ? What additional info do we want to show ?
        for (int i = 0; i < locations.length; ++i) {
            Location l = locations[i];
            updateMinAndMaxAltitude(l.getAltitude());
            if (i != 0)
                totalDistance += l.distanceTo(locations[i - 1]);
        }
        // TODO: the altitudes completely off right now, try to fix
        totalAltitudeChange = maxAltitude - minAltitude;
    }

    /**
     * Updates the max and min altitudes according to a new altitude
     *
     * @param a the new altitude
     */
    private void updateMinAndMaxAltitude(double a) {
        if (a < minAltitude)
            minAltitude = a;
        if (a > maxAltitude)
            maxAltitude = a;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // Make map static
        map.getUiSettings().setAllGesturesEnabled(false);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });
        // Adapt padding to fit markers
        map.setPadding(50, 150, 50, 50);
        handleExtras();
        drawTrackOnMap();
    }

    /**
     * Draws the full track and markers on the map
     */
    private void drawTrackOnMap() {
        if (map != null && points != null) {
            // Get correct zoom
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (LatLng point : points)
                boundsBuilder.include(point);
            LatLngBounds bounds = boundsBuilder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = (int)(getResources().getDisplayMetrics().heightPixels * 0.65);
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, 0));
            // Add lines
            map.addPolyline(new PolylineOptions().addAll(Arrays.asList(points)));
            // Add markers (start = green, finish = red)
            map.addMarker(new MarkerOptions().position(points[0]).icon(defaultMarker(150)).alpha(0.8f));
            map.addMarker(new MarkerOptions().position(points[points.length - 1]).icon(defaultMarker(20)).alpha(0.8f));
        }
    }
}
