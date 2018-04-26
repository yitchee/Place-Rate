package c15390501.placerate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {
    User user = new User();
    TextView tvName, tvLevel, tvNum;
    ImageButton btnCapture;
    Cursor c;
    int CAMERA_CODE = 1888;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = new Database(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        user.setUsername(extras.getString("Username"));

        //checking for and requesting permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},1);
            }
        }

        btnCapture = findViewById(R.id.pictureButton);
        btnCapture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //starts camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_CODE);
            }
        });

        tvName = findViewById(R.id.username);
        tvLevel = findViewById(R.id.level);
        tvNum = findViewById(R.id.numRated);

        db.open();

        //get user data
        db.getUserInfo(user.getUsername());
        c = db.getUserInfo(user.getUsername());
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                user.setNumRated(c.getInt(c.getColumnIndex(db.KEY_UNUMRATED)));
                c.moveToNext();
            }
        }
        user.setLevel(user.getNumRated()/3);
        Cursor cursor = db.getPicture(user.getUsername());
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            try {
                //gets byte array from db and converts to Bitmap to display (if available)
                byte[] imgByte = cursor.getBlob(cursor.getColumnIndex(db.KEY_IIMAGE));
                if (imgByte != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                    btnCapture.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        db.close();

        tvName.setText(user.getUsername());
        tvNum.setText(String.valueOf(user.getNumRated()));
        tvLevel.setText(String.valueOf(user.getLevel()));
    }

    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        //gets data from camera
        if (reqCode == CAMERA_CODE) {
            db.open();
            if(resCode == RESULT_OK) {
                //sets ImageView to image taken
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                btnCapture.setImageBitmap(photo);
                //converts Bitmap to byte array to store as BLOB in sqlite database
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                try {
                    //replace picture in db
                    db.insertPicture(user.getUsername(), byteArray);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Cannot change picture in database." + user.getUsername(),
                            Toast.LENGTH_LONG).show();
                }
            }
            if (resCode == RESULT_CANCELED) {
            }
            db.close();
        }
    }
}
