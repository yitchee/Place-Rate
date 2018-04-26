package c15390501.placerate;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SavedLocationsActivity extends ListActivity {
    ArrayList<LocationInfo> locations;
    ListAdapter adapter;
    Cursor c;
    User user;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        //gets data passed from parent activity
        user = new User();
        Bundle extra = getIntent().getExtras();
        user.setUsername(extra.getString("Username"));
        db = new Database(getApplicationContext());
    }

    //Will update the list when back in focus (and when started)
    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    protected void updateList() {
        //gets all saved locations linked to user, and sets to list view
        db.open();
        locations = new ArrayList<LocationInfo>();
        c = db.getLocations(user.getUsername());
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                LocationInfo loc = new LocationInfo();
                loc.setName(c.getString(c.getColumnIndex(db.KEY_LLOCNAME)));
                loc.setId(c.getInt(c.getColumnIndex(db.KEY_LROWID)));
                loc.setAddress(c.getString(c.getColumnIndex(db.KEY_LLOCADDRESS)));
                loc.setType(c.getString(c.getColumnIndex(db.KEY_LLOCTYPE)));
                loc.setRating(c.getInt(c.getColumnIndex(db.KEY_LLOCSTARS)));
                locations.add(loc);
                c.moveToNext();
            }
        }
        db.close();
        adapter = new CustomAdapter(this, R.layout.row_list, locations);
        setListAdapter(adapter);
    }

    protected void onListItemClick (ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id); // always 1st line

        //starts activity that shows individual location details
        int lId = locations.get(position).getId();
        Intent intent = new Intent(getApplicationContext(), LocationDetailsActivity.class);
        intent.putExtra("ID", lId);
        intent.putExtra("Username", user.getUsername());
        startActivity(intent);
    }

    public class CustomAdapter extends ArrayAdapter<LocationInfo> {
        CustomAdapter (Context context, int rowLayoutId, ArrayList<LocationInfo> locations) {
            super(context, rowLayoutId, locations);
        }

        @Override
        public View getView(int pos, View v, ViewGroup parent) {
            View row;
            LayoutInflater inflater = getLayoutInflater();
            row = inflater.inflate(R.layout.row_list, parent, false);

            TextView tvName = row.findViewById(R.id.locName);
            ImageView ivRating = row.findViewById(R.id.rating);

            //sets image to use depending on user's ratings
            int locRating = locations.get(pos).getRating();
            switch (locRating) {
                case 1:
                    ivRating.setImageResource(R.drawable.star1);
                    break;
                case 2:
                    ivRating.setImageResource(R.drawable.star2);
                    break;
                case 3:
                    ivRating.setImageResource(R.drawable.star3);
                    break;
                case 4:
                    ivRating.setImageResource(R.drawable.star4);
                    break;
                case 5:
                    ivRating.setImageResource(R.drawable.star5);
                    break;
                default:
                    ivRating.setImageResource(R.drawable.star);
                    break;
            }
            tvName.setText(locations.get(pos).getName());

            return row;
        }
    }//end CustomAdapter
}
