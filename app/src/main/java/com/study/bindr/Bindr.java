package com.study.bindr;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Bindr extends AppCompatActivity {
    //Need this for our drawer layout
    //private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /* Start Navigation Stuff
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
         End Navigation Stuff */

    }

    public void login(View view) {
        Intent intent = new Intent(Bindr.this, Home_Activity.class);
        startActivity(intent);
    }

    public void register(View view) {
        Intent intent = new Intent(Bindr.this, RegisterActivity.class);
        startActivity(intent);
    }
}
