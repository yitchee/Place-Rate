package c15390501.placerate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//Used to keep a user logged in
//Reference: The following code is from
//https://stackoverflow.com/questions/35743615/show-login-activity-only-once-and-on-next-start-main-activity
public class SplashActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // decide here whether to navigate to Login or MainMenu Activity
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if (pref.getBoolean("activity_executed", false))
        {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Intent intent = new Intent(this, StartUpActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
//Reference complete