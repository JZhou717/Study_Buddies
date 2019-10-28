package com.study.bindr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.google.android.material.navigation.NavigationView;

public class Bindr extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Need this for our drawer layout
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /* Start Navigation Stuff */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Change the view to the proper screen
        drawer = findViewById(R.id.login_screen);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Change the selection to the proper screen
        //navigationView.setCheckedItem(R.id.nav_courses);
        /* End Navigation Stuff */

    }

    public void login(View view) {
        Intent intent = new Intent(Bindr.this, Home_Activity.class);
        startActivity(intent);
    }

    public void register(View view) {
        Intent intent = new Intent(Bindr.this, RegisterActivity.class);
        startActivity(intent);
    }

    /* Start Navigation Stuff */
    //Navbar closes on activity change
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {
            case R.id.nav_home:
                intent = new Intent(Bindr.this, Home_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                intent = new Intent(Bindr.this, UserProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_courses:
                intent = new Intent(Bindr.this, EditCoursesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_session:
                intent = new Intent(Bindr.this, Session_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_chatslist:
                intent = new Intent(Bindr.this, ChatsListActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                intent = new Intent(Bindr.this, Bindr.class);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* End Navigation Stuff */
}
