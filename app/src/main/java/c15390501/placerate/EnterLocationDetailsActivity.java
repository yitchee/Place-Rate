package c15390501.placerate;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.PriorityQueue;

public class EnterLocationDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{
    private User user;
    private Database db;
    private Spinner spnRating, spnType;
    private EditText etName, etAddress;
    private Button btnDone, btnCancel;
    private static final String[]rpaths = {"5", "4", "3", "2", "1"};
    private static final String[]tpaths = {"Airport", "Attraction", "Amusement Park", "Bakery", "Bar", "Cafe", "Casino", "Cinema",
            "Clothes Store", "Food", "Gym", "Health-care", "Hotel", "House", "Library", "Monument", "Museum", "Nightclub", "Park",
            "Restaurant", "School", "Shopping Centre", "Stadium", "University", "Zoo", "Other"};
    private int rating;
    private String address, name, type, mode;
    private double lat, lon;
    private LocationInfo newLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_location_details);

        user = new User();
        newLoc = new LocationInfo();
        db = new Database(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        mode = extras.getString("Mode");

        if (mode.equals("gps")) {
            name = extras.getString("Name");
            address = extras.getString("Address");
            lat = extras.getDouble("Latitude");
            lon = extras.getDouble("Longitude");
        }
        user.setUsername(extras.getString("Username"));

        //Setting up buttons
        btnDone = (Button) findViewById(R.id.doneButton);
        btnCancel = (Button) findViewById(R.id.cancelButton);
        btnDone.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        //Setting up the spinners
        spnRating = (Spinner)findViewById(R.id.rating);
        ArrayAdapter<String> adapterRating = new ArrayAdapter<String>(EnterLocationDetailsActivity.this, android.R.layout.simple_spinner_item, rpaths);
        spnType = (Spinner)findViewById(R.id.type);
        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(EnterLocationDetailsActivity.this, android.R.layout.simple_spinner_item, tpaths);
        adapterRating.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRating.setAdapter(adapterRating);
        spnType.setAdapter(adapterType);
        spnRating.setOnItemSelectedListener(this);
        spnType.setOnItemSelectedListener(this);

        //Setting up edit text fields
        etName = (EditText) findViewById(R.id.name);
        etAddress = (EditText) findViewById(R.id.address);

        etAddress.setText(address);
        etName.setText(name);
    }

    //Get rating of location to item on spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        rating = Integer.parseInt(spnRating.getSelectedItem().toString());
        type = (spnType.getSelectedItem().toString());
    }

    @Override
    public void onClick(View v) {
        Intent returnIntent = new Intent();
        if (v == (View) btnDone) {
            db.open();

            setResult(Activity.RESULT_OK, returnIntent);
            if (mode.equals("gps")) {
                if (!checkInput()) {
                    Toast.makeText(getApplicationContext(), "ERROR: All fields must be filled.", Toast.LENGTH_LONG).show();
                } else {
                    newLoc.setName(etName.getText().toString());
                    newLoc.setAddress(etAddress.getText().toString());
                    newLoc.setType(type);
                    newLoc.setLatitude(lat);
                    newLoc.setLongitude(lon);
                    newLoc.setRating(rating);

                    try {
                        long res = db.addLocation(user.getUsername(), newLoc);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR: Could not save location.", Toast.LENGTH_LONG).show();
                    }

                    db.close();
                    finish();
                }
            }
            else if (mode.equals("manual")) {
                if (!checkInput()) {
                    Toast.makeText(getApplicationContext(), "ERROR: All fields must be filled.", Toast.LENGTH_LONG).show();
                } else {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses = null;
                    String userAddress = etAddress.getText().toString();
                    //tries to get location from user inputted address
                    try {
                        addresses = geocoder.getFromLocationName(userAddress, 1);
                        newLoc.setName(etName.getText().toString());
                        newLoc.setAddress(etAddress.getText().toString());
                        newLoc.setType(type);
                        newLoc.setLatitude(addresses.get(0).getLatitude());
                        newLoc.setLongitude(addresses.get(0).getLongitude());
                        newLoc.setRating(rating);
                        long res = db.addLocation(user.getUsername(), newLoc);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR: Cannot get location from address", Toast.LENGTH_LONG).show();
                    }

                    db.close();
                    finish();
                }
            }
            db.close();
        }
        else if (v == (View) btnCancel) {
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    //checks for empty input
    public boolean checkInput() {
        if (etName.getText().toString().trim().equals("") || etAddress.getText().toString().trim().equals("")) {
            return false;
        } else {
            return true;
        }
    }
    //Disables back button
    @Override
    public void onBackPressed() {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
