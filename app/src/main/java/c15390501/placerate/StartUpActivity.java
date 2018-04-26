package c15390501.placerate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartUpActivity extends AppCompatActivity {
    //used to direct user to 'Login' or 'Create Account'
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
    }

    public void goToLogin(View v)
    {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);

        //Kills activity so when user presses back button it won't 'Logout'
        finish();
    }

    public void goToCreate(View v)
    {
        Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
        startActivity(intent);

        //Kills activity so when user presses back button it won't 'Logout'
        finish();
    }
}
