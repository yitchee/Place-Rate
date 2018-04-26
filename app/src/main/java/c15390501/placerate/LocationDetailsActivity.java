package c15390501.placerate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Paint;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationDetailsActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback {
    Database db;
    String username;
    TextView tvName, tvAddress, tvType;
    ImageView ivStar1, ivStar2, ivStar3, ivStar4, ivStar5;
    ImageButton ibEdit, ibDelete;
    int locId;
    LocationInfo locationInfo;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        locationInfo = new LocationInfo();
        //setting up components
        tvName = findViewById(R.id.name);
        tvAddress = findViewById(R.id.address);
        tvType = findViewById(R.id.type);
        ivStar1 = findViewById(R.id.star1);
        ivStar2 = findViewById(R.id.star2);
        ivStar3 = findViewById(R.id.star3);
        ivStar4 = findViewById(R.id.star4);
        ivStar5 = findViewById(R.id.star5);
        ibEdit = findViewById(R.id.edit);
        ibDelete = findViewById(R.id.delete);
        ibEdit.setOnClickListener(this);
        ibDelete.setOnClickListener(this);

        db = new Database(getApplicationContext());

        Bundle extra = getIntent().getExtras();
        locId = extra.getInt("ID");
        username = extra.getString("Username");
    }

    @Override
    public void onClick(View v) {
        if (v == ibEdit) {
            //calls edit activity
            Intent intent = new Intent(getApplicationContext(), EditPopupActivity.class);
            intent.putExtra("LocId", locId);
            intent.putExtra("Name", locationInfo.getName());
            intent.putExtra("Address", locationInfo.getAddress());
            intent.putExtra("Type", locationInfo.getType());
            intent.putExtra("Rating", locationInfo.getRating());

            startActivity(intent);
        }
        else if (v == ibDelete) {
            //calls delete activity
            AlertDialog diaBox = AskOption();
            diaBox.show();
        }
    }

    private void setStarRating() {
        //Sets image views based on location rating
        switch (locationInfo.getRating()) {
            case 1:
                ivStar1.setImageResource(R.drawable.star);
                ivStar2.setImageResource(R.drawable.emptystar);
                ivStar3.setImageResource(R.drawable.emptystar);
                ivStar4.setImageResource(R.drawable.emptystar);
                ivStar5.setImageResource(R.drawable.emptystar);
                break;
            case 2:
                ivStar1.setImageResource(R.drawable.star);
                ivStar2.setImageResource(R.drawable.star);
                ivStar3.setImageResource(R.drawable.emptystar);
                ivStar4.setImageResource(R.drawable.emptystar);
                ivStar5.setImageResource(R.drawable.emptystar);
                break;
            case 3:
                ivStar1.setImageResource(R.drawable.star);
                ivStar2.setImageResource(R.drawable.star);
                ivStar3.setImageResource(R.drawable.star);
                ivStar4.setImageResource(R.drawable.emptystar);
                ivStar5.setImageResource(R.drawable.emptystar);
                break;
            case 4:
                ivStar1.setImageResource(R.drawable.star);
                ivStar2.setImageResource(R.drawable.star);
                ivStar3.setImageResource(R.drawable.star);
                ivStar4.setImageResource(R.drawable.star);
                ivStar5.setImageResource(R.drawable.emptystar);
                break;
            case 5:
                ivStar1.setImageResource(R.drawable.star);
                ivStar2.setImageResource(R.drawable.star);
                ivStar3.setImageResource(R.drawable.star);
                ivStar4.setImageResource(R.drawable.star);
                ivStar5.setImageResource(R.drawable.star);
                break;
            default:
                ivStar1.setImageResource(R.drawable.emptystar);
                ivStar2.setImageResource(R.drawable.emptystar);
                ivStar3.setImageResource(R.drawable.emptystar);
                ivStar4.setImageResource(R.drawable.emptystar);
                ivStar5.setImageResource(R.drawable.emptystar);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in selected location and move the camera to it
        LatLng selectedLoc = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());
        mMap.addMarker(new MarkerOptions().position(selectedLoc).title(locationInfo.getName() + " Marker"));
        //set camera zoom and position
        CameraPosition cameraPosition = new CameraPosition.Builder().target(selectedLoc).zoom(12.5f).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onResume() {
        super.onResume();
        db.open();
        //refreshes activity when back in focus
        Cursor c = db.getLocationDetails(locId);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                locationInfo.setName(c.getString(c.getColumnIndex(db.KEY_LLOCNAME)));
                locationInfo.setAddress(c.getString(c.getColumnIndex(db.KEY_LLOCADDRESS)));
                locationInfo.setType(c.getString(c.getColumnIndex(db.KEY_LLOCTYPE)));
                locationInfo.setRating(c.getInt(c.getColumnIndex(db.KEY_LLOCSTARS)));
                locationInfo.setLatitude(c.getDouble(c.getColumnIndex(db.KEY_LLATITUDE)));
                locationInfo.setLongitude(c.getDouble(c.getColumnIndex(db.KEY_LLONGTITUDE)));
                c.moveToNext();
            }
        }

        tvName.setText(locationInfo.getName());
        tvName.setPaintFlags(tvName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvAddress.setText(locationInfo.getAddress());
        tvType.setText(locationInfo.getType());

        setStarRating();
    }

    private AlertDialog AskOption() {
        //alert to confirm deleting location
        AlertDialog confirmDeleteDialog =new AlertDialog.Builder(this).setTitle("Delete").setMessage("Confirm delete?").setIcon(R.drawable.delete)
                //if delete s is confirmed
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            db.deleteLocation(locId, username);
                            Toast.makeText(getApplicationContext(), "Delete successful", Toast.LENGTH_LONG).show();
                            db.close();
                            finish();
                        } catch (SQLException e) {
                            Toast.makeText(getApplicationContext(), "ERROR: Could not delete.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        dialog.dismiss();
                    }
                })
                //if 'Cancel' is clicked
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return confirmDeleteDialog;
    }
}
