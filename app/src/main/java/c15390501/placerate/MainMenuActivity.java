package c15390501.placerate;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
    User user = new User();
    Button btnGps, btnManual, btnViewSaved;
    LocationInfo newLocation;
    LocationManager locationManager;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        db = new Database(getApplicationContext());

        btnViewSaved = (Button) findViewById(R.id.viewSaved);
        btnViewSaved.setOnClickListener(this);

        String uname;
        String pw = "";
        //gets data from activity
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        uname = preferences.getString("Username", "");

        if (uname == null) {
            Bundle extra = getIntent().getExtras();
            uname = extra.getString("Username");
            pw = extra.getString("Password");
        }

        user.setUsername(uname);
        user.setPassword(pw);

        setUpLocation();
    }

    @Override
    public void onClick(View v) {
        if (v == (View) btnGps) {
            newLocation = getCoordinates();
            addLocationGPS(newLocation);
        }
        else if (v == (View) btnManual) {
            addLocationManual();
        }
        else if (v == (View) btnViewSaved) {
            Intent intent = new Intent(getApplicationContext(), SavedLocationsActivity.class);
            intent.putExtra("Username", user.getUsername());
            startActivity(intent);
        }
    }

    //Displays popup for adding location (embedded listener)
    public void add(View v) {
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.activity_main_menu_layout);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.add_location_popup, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

        btnGps = (Button) popupView.findViewById(R.id.gpsButton);
        btnManual = (Button) popupView.findViewById(R.id.manualButton);
        btnGps.setOnClickListener(this);
        btnManual.setOnClickListener(this);
    }

    //gets current position using GPS
    public LocationInfo getCoordinates() {
        LocationInfo locationInfo = new LocationInfo();

        List<String> providers = locationManager.getAllProviders();

        //check and request permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MainMenuActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else {
            int i = 0;
            if (!providers.isEmpty()) {
                ActivityCompat.requestPermissions(MainMenuActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                //get longitude and latitude is available
                Location location = locationManager.getLastKnownLocation(providers.get(i));
                double lat = 0;
                double lon = 0;
                try {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "ERROR: No providers available.", Toast.LENGTH_SHORT).show();
                }

                locationInfo.setLatitude(lat);
                locationInfo.setLongitude(lon);
            }
            else {
                Toast.makeText(getApplicationContext(), "ERROR: No providers available.", Toast.LENGTH_SHORT).show();
            }
        }
        return locationInfo;
    }

    //checking for and requesting permissions
    private void setUpLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            }
            else {
                //request permissions
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }
    }

    //starts new activity with no location data sent
    private  void addLocationManual() {
        Intent intent = new Intent(getApplicationContext(), EnterLocationDetailsActivity.class);
        intent.putExtra("Mode", "manual");
        intent.putExtra("Username", user.getUsername());
        startActivityForResult(intent, 1);
    }

    //starts new activity with location data sent
    private void addLocationGPS(LocationInfo newLoc) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        double latitude, longitude;
        try {
            latitude = newLoc.getLatitude();
            longitude = newLoc.getLongitude();
            //gets address from geocoder using coordinates
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            try {
                String address = addresses.get(0).getAddressLine(0);
                String knownName = addresses.get(0).getFeatureName();

                //passes relevant data to new activity
                Intent intent = new Intent(getApplicationContext(), EnterLocationDetailsActivity.class);
                intent.putExtra("Mode", "gps");
                intent.putExtra("Name", knownName);
                intent.putExtra("Address", address);
                intent.putExtra("Latitude", latitude);
                intent.putExtra("Longitude", longitude);
                intent.putExtra("Username", user.getUsername());
                startActivityForResult(intent, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void viewProfile(View v) {
        //opens profile screen
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.putExtra("Username", user.getUsername());
        startActivity(intent);
    }

    public void logout(View v) {
        //set activity_executed: used to keep user logged in / out
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean("activity_executed", false);
        edt.apply();

        Intent intent = new Intent(getApplicationContext(), StartUpActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            //starts list activity after receiving result from called activity
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(getApplicationContext(), SavedLocationsActivity.class);
                intent.putExtra("Username", user.getUsername());
                startActivity(intent);
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    //stop updates to save battery
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    //restart updates when back in focus
    @Override
    protected void onResume() {
        super.onResume();

        setUpLocation();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}