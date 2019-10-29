package com.study.bindr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;

public class Home_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Need this for our drawer layout
    private DrawerLayout drawer;
    private ScrollView courseScrollView;
    private Button enterCourseButton1;
    private Button enterCourseButton2;
    private Button emptyCourse1;
    private Button emptyCourse2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_);

        /* Start Navigation Stuff */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Courses");

        //Change the view to the proper screen
        drawer = findViewById(R.id.home_screen);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Change this to the proper page
        navigationView.setCheckedItem(R.id.nav_home);
        /* End Navigation Stuff */

        courseScrollView = findViewById(R.id.courseScrollView);
        enterCourseButton1 = findViewById(R.id.enterCourseButton1);
        enterCourseButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home_Activity.this, MatchActivity.class);
                startActivity(intent);
            }
        });
        enterCourseButton2 = findViewById(R.id.enterCourseButton2);
        enterCourseButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home_Activity.this, MatchActivity.class);
                startActivity(intent);
            }
        });
        emptyCourse1 = findViewById(R.id.emptyCourse1);
        emptyCourse1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alert = new AlertDialog.Builder(Home_Activity.this).create();
                alert.setTitle("Alert");
                alert.setMessage("This course is currently empty");
                alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alert.show();
            }
        });
        emptyCourse2 = findViewById(R.id.emptyCourse2);
        emptyCourse2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alert = new AlertDialog.Builder(Home_Activity.this).create();
                alert.setTitle("Alert");
                alert.setMessage("This course is currently empty");
                alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alert.show();
            }
        });
    }

    /* Start Navigation Stuff */
    //Navbar closes on activity change

    @Override
    public void onStart(){
        super.onStart();

        String[] courses = {"Software Methodology","Software Engineering","Internet Technology","Intro to AI"};

    }

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
                intent = new Intent(Home_Activity.this, Home_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                intent = new Intent(Home_Activity.this, UserProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_courses:
                intent = new Intent(Home_Activity.this, EditCoursesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_session:
                intent = new Intent(Home_Activity.this, Session_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_chatslist:
                intent = new Intent(Home_Activity.this, ChatsListActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                intent = new Intent(Home_Activity.this, Bindr.class);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* End Navigation Stuff */
}
