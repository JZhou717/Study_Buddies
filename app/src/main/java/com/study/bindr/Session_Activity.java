package com.study.bindr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.google.android.material.navigation.NavigationView;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Session;
import model.Student;

public class Session_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Need this for our drawer layout
    private DrawerLayout drawer;
    private ListView sessionListView;
    private SessionAdapter sessionAdapter;

    private ArrayList<Session> sessions = new ArrayList<>();

    //CURRENT STUDENT TEST
    private String id = "5ddb3fd5c3de9037b0b2ced6";
    private Student me = new Student(id);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_);

        /* Start Navigation Stuff */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Study Sessions");

        //Change the view to the proper screen
        drawer = findViewById(R.id.session_screen);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Change this to the proper page
        navigationView.setCheckedItem(R.id.nav_session);
        /* End Navigation Stuff */

        sessionListView = findViewById(R.id.sessionListView);
        populateSessions();

    }

    private void populateSessions(){
        me.getSessions(new DatabaseCallBack<List<Document>>() {
            @Override
            public void onCallback(List<Document> items) {
                for (int i = 0; i < items.size(); i++){
                    String partnerID = items.get(i).get("partner").toString();
                    Date dateTime = (Date) items.get(i).get("datetime");
                    int reminder = Integer.parseInt(items.get(i).get("reminder").toString());
                    Session session = new Session(partnerID, dateTime, reminder);
                    sessions.add(session);
                }
                sessionAdapter = new SessionAdapter(Session_Activity.this,sessions);
                sessionListView.setAdapter(sessionAdapter);
            }
        });
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
                intent = new Intent(Session_Activity.this, Home_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                intent = new Intent(Session_Activity.this, UserProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_courses:
                intent = new Intent(Session_Activity.this, EditCoursesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_session:
                intent = new Intent(Session_Activity.this, Session_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_chatslist:
                intent = new Intent(Session_Activity.this, ChatsListActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                intent = new Intent(Session_Activity.this, Bindr.class);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
