package c15390501.placerate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    Database db;
    EditText etUsername, etPassword;
    Button btnCreate, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        db = new Database(getApplicationContext());

        //components setup
        etUsername = (EditText)findViewById(R.id.username);
        etPassword = (EditText) findViewById(R.id.password);

        btnCreate = (Button) findViewById(R.id.createButton);
        btnCancel = (Button) findViewById(R.id.cancelButton);
        btnCreate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == (View) btnCreate) {
            insertUser();
        }
        else if (v == (View) btnCancel) {
            goBack();
        }
    }

    public void insertUser()
    {
        db = db.open();

        try {
            //checks if username already exists
            Cursor c = db.getUser(etUsername.getText().toString(), etPassword.getText().toString());
            if (c != null && c.getCount() > 0) {
                Toast.makeText(getApplicationContext(), "ERROR: Username taken.", Toast.LENGTH_LONG).show();
            } else {
                long res = db.createUser(etUsername.getText().toString(), etPassword.getText().toString());

                String name = etUsername.getText().toString();
                String pw = etPassword.getText().toString();
                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                intent.putExtra("Username", name);
                intent.putExtra("Password", pw);

                //set activity_executed: used to keep user logged in / out
                SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();
                edt.putBoolean("activity_executed", true);
                edt.apply();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Username", etUsername.getText().toString());
                editor.apply();

                db.close();

                startActivity(intent);

                //Kills activity so when user presses back button it won't 'Logout'
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Invalid input, letters and numbers only.", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    //back to main menu
    public void goBack()
    {
        Intent intent = new Intent(getApplicationContext(), StartUpActivity.class);
        startActivity(intent);
        finish();
    }

    //overrides back button function
    @Override
    public void onBackPressed() {
        goBack();
    }
}