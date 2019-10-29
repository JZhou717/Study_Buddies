package com.study.bindr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class ChatboxActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button blockButton;
    private Button studyButton;

    //Need this for our drawer layout
    private DrawerLayout drawer;

    DialogInterface.OnClickListener blockClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbox);

        /* Start Navigation Stuff */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messages");

        //Change the view to the proper screen
        drawer = findViewById(R.id.chatbox_screen);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Change this to the proper page
        navigationView.setCheckedItem(R.id.nav_home);
        /* End Navigation Stuff */

        blockClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(ChatboxActivity.this, "Successfully Blocked", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChatboxActivity.this, ChatsListActivity.class);
                        startActivity(intent);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };


    }

    public void sendMessage(View view) {

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
            case R.id.nav_courses:
                intent = new Intent(ChatboxActivity.this, EditCoursesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                intent = new Intent(ChatboxActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_home:
                intent = new Intent(ChatboxActivity.this, Home_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_session:
                intent = new Intent(ChatboxActivity.this, Session_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_chatslist:
                intent = new Intent(ChatboxActivity.this, ChatsListActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                intent = new Intent(ChatboxActivity.this, Bindr.class);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /* End Navigation Stuff */


    public void onBlockClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatboxActivity.this);
        builder.setMessage("Are you sure you want to block?").setPositiveButton("Yes", blockClickListener)
                .setNegativeButton("No", blockClickListener).show();
    }

    public void onStudySessionClick(View view) {
        Intent intent = new Intent(ChatboxActivity.this, SetStudySessionActivity.class);
        startActivity(intent);
    }

}
