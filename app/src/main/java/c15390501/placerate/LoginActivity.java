package c15390501.placerate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Database db;
    EditText etUsername, etPassword;
    Button btnLogin, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new Database(getApplicationContext());

        //components setup
        etUsername = (EditText)findViewById(R.id.username);
        etPassword = (EditText) findViewById(R.id.password);

        btnLogin = (Button) findViewById(R.id.loginButton);
        btnCancel = (Button) findViewById(R.id.cancelButton);
        btnLogin.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == (View) btnLogin) {
            login();
        }
        else if (v == (View) btnCancel) {
            goBack();
        }
    }

    public void login() {
        db = db.open();

        try {
            //checks if account exists
            Cursor c = db.getUser(etUsername.getText().toString(), etPassword.getText().toString());
            if (c != null && c.getCount() > 0) {
                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                intent.putExtra("Username", etUsername.getText().toString());
                intent.putExtra("Password", etPassword.getText().toString());

                //set activity_executed: used to keep user logged in / out
                SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();
                edt.putBoolean("activity_executed", true);
                edt.apply();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Username", etUsername.getText().toString());
                editor.apply();

                startActivity(intent);

                //Kills activity so when user presses back button it won't 'Logout'
                finish();

                db.close();
            } else {
                Toast.makeText(getApplicationContext(), "Username or password is incorrect.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Invalid input, letters and numbers only.", Toast.LENGTH_LONG).show();
        }
    }

    public void goBack() {
        Intent intent = new Intent(getApplicationContext(), StartUpActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
