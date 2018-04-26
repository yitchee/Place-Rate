package c15390501.placerate;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by YitChee on 22/11/2017.
 */

public class EditPopupActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    EditText etName;
    Spinner spnRating, spnType;
    private static final String[]rpaths = {"5", "4", "3", "2", "1"};
    private static final String[]tpaths = {"Airport", "Attraction", "Amusement Park", "Bakery", "Bar", "Cafe", "Casino", "Cinema",
            "Clothes Store", "Food", "Gym", "Health-care", "Hotel", "House", "Library", "Monument", "Museum", "Nightclub", "Park",
            "Restaurant", "School", "Shopping Centre", "Stadium", "University", "Zoo", "Other"};
    Database db;
    Button btnSave, btnCancel;
    LocationInfo newLoc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpopup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        newLoc = new LocationInfo();
        Bundle extras = getIntent().getExtras();
        newLoc.setId(extras.getInt("LocId"));
        newLoc.setName(extras.getString("Name"));
        newLoc.setAddress(extras.getString("Address"));
        newLoc.setType(extras.getString("Type"));
        newLoc.setRating(extras.getInt("Rating"));

        //Setting up the spinners
        spnRating = (Spinner)findViewById(R.id.rating);
        ArrayAdapter<String> adapterRating = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, rpaths);
        spnType = (Spinner)findViewById(R.id.type);
        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, tpaths);
        adapterRating.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRating.setAdapter(adapterRating);
        spnType.setAdapter(adapterType);
        spnRating.setOnItemSelectedListener(this);
        spnType.setOnItemSelectedListener(this);

        etName = (EditText) findViewById(R.id.name);
        etName.setText(newLoc.getName());

        //setting size of popup window
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.85), (int)(height*.85));

        btnSave = findViewById(R.id.saveButton);
        btnSave.setOnClickListener(this);
        btnCancel = findViewById(R.id.cancelButton);
        btnCancel.setOnClickListener(this);

        db = new Database(getApplicationContext());
        db.open();
    }

    @Override
    public void onClick(View v) {
        if (v == (View) btnSave) {
            db.open();
            //updating selected location
            if (etName.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "ERROR: All fields must be filled.", Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    newLoc.setName(etName.getText().toString());
                    db.updateLocation(newLoc);
                    db.close();
                    finish();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "ERROR: Could not update location.", Toast.LENGTH_LONG).show();
                }
            }
        }
        else if (v == (View) btnCancel) {
            finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //set rating and type depending on selected item
        newLoc.setRating(Integer.parseInt(spnRating.getSelectedItem().toString()));
        newLoc.setType(spnType.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
