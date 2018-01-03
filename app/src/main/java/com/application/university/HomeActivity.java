package com.application.university;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.application.university.Misc.Methods;
import com.application.university.models.Pupil;
import com.application.university.models.Pupil;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

public class HomeActivity extends AppCompatActivity {
    private Pupil currentPupil;
    private CoordinatorLayout coordinator;
    private Menu menu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_home);
        getPupilModel();
        getReferences();

    }

    //gets user model
    private void getPupilModel() {
        currentPupil = Methods.getPupilModel(getApplicationContext());

    }

    //Method to logout of the app and also from fb sdk
    public void logout(View view) {
        LoginManager.getInstance().logOut();
        Methods.logout(this);
    }

    //gets references of all the views
    public void getReferences() {
        //showing create session option if current pupil is also a host
        if (currentPupil.getIsHost()) menu.findItem(R.id.action_create_session).setVisible(true);

        //getting references of views
        coordinator = (CoordinatorLayout) findViewById(R.id.HomeActivity_coordinator);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                Toast.makeText(HomeActivity.this, "Home Action", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.action_profile:
                                Toast.makeText(HomeActivity.this, "Profile Action", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });
    }

    //creates session
    private void createSession() {
        if (!currentPupil.getIsHost()) {
            //display not pupil error
            Snackbar.make(coordinator, "Not a host", Snackbar.LENGTH_LONG).show();
            return;
        }
        Methods.goToCreateSessionActivity(getApplicationContext());
    }

    //adds option menu to toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        menu.findItem(R.id.action_create_session).setVisible(false);
        this.menu = menu;
        return true;
    }

    //menu actions for toolbar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_create_session:
                createSession();
                break;
        }

        return true;
    }
}
