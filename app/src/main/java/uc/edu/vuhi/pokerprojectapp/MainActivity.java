package uc.edu.vuhi.pokerprojectapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import uc.edu.vuhi.pokerprojectapp.UTIL.Utility;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @BindView(R.id.toolbarMain)
    Toolbar toolbarMain;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbarMain);
        getSupportActionBar().setTitle("Main Page");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Utility.sendTo(MainActivity.this, LoginActivity.class);
        }
    }


    /**
     * This will be invoked when the user clicks the  menu
     *
     * @param menuItem
     */
    public void accountSettingClick(MenuItem menuItem) {
        // Explict intent to invoke another screen.
        // Change MainActivity.class to AccountSettingsActivity.class once it's made

        Intent accountSettingsIntent = new Intent(this, MainActivity.class);
        startActivity(accountSettingsIntent);

        // finish();
    }

    public void rechargeTokenClick(MenuItem menuItem) {
        // Explict intent to invoke another screen.
        // Change MainActivity.class to rechargeTokenActivity.class once it's made

        Intent rechargeTokenIntent = new Intent(this, MainActivity.class);
        startActivity(rechargeTokenIntent);

        // finish();
    }

    public void logoutClick(MenuItem menuItem) {
        // Explict intent to invoke another screen. Can we just close the app from this method?
        // Change MainActivity.class to logoutActivity.class once it's made
        logOut();

/*        Intent logoutIntent = new Intent(this, MainActivity.class);
        startActivity(logoutIntent);*/

        // finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnLogout:
                logOut();
                return super.onOptionsItemSelected(item);

            case R.id.btnAccountSetting:
                Utility.sendTo(this, SetUpActivity.class);
                return super.onOptionsItemSelected(item);
            default:
                return false;
        }

    }


    private void logOut() {
        mAuth.signOut();
        Utility.sendTo(this, LoginActivity.class);
    }
}
