package com.study.bindr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ChatsListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ChatsAdapter.OnChatListener,
        MatchedStudentAdapter.OnMatchIconListener {
    //Need this for our drawer layout
    private DrawerLayout drawer;

    private RecyclerView matchedStudentsRecyclerView;
    private RecyclerView.Adapter matchedStudentAdapter;

    private RecyclerView chatsRecyclerView;
    private RecyclerView.Adapter chatsAdapter;

    private ArrayList<Student> matchedStudentsList = new ArrayList<>();
    private ArrayList<Student> chatsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_list);

        /* Start Navigation Stuff */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chats");

        //Change the view to the proper screen
        drawer = findViewById(R.id.chatslist_screen);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Change this to the proper page
        navigationView.setCheckedItem(R.id.nav_chatslist);
        /* End Navigation Stuff */


        //Dropdown for filter
        final Spinner searchByDropDown=findViewById(R.id.searchByDropDown);
        List<String> searchOptions = new ArrayList<String>();
        searchOptions.add("User");
        searchOptions.add("Course Name");

        ArrayAdapter<String> searchOptionsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, searchOptions);

        searchOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        searchByDropDown.setAdapter(searchOptionsAdapter);


        //Populate student matches (not yet messaged)
        matchedStudentsRecyclerView = findViewById(R.id.matchedRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        matchedStudentsRecyclerView.setLayoutManager(layoutManager);

        matchedStudentsList.add(new Student("Tom", "Moore"));
        matchedStudentsList.add(new Student("Molly ", "Flower"));
        matchedStudentsList.add(new Student("Jessica", "Warner"));
        matchedStudentsList.add(new Student("Linda ", "Wallace"));
        matchedStudentsList.add(new Student("Ben ", "Grey"));

        matchedStudentAdapter=new MatchedStudentAdapter(matchedStudentsList, this, this);
        matchedStudentsRecyclerView.setAdapter(matchedStudentAdapter);

        //Populate chats
        chatsRecyclerView = findViewById(R.id.chatsRecyclerView);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatsRecyclerView.setLayoutManager(layoutManager);

        chatsList.add(new Student("Billy", "Red"));
        chatsList.add(new Student("Cindy ", "Potts"));
        chatsList.add(new Student("Danny", "Blue"));
        chatsList.add(new Student("Sunny ", "Renner"));
        chatsList.add(new Student("Greg", "Lee"));

        chatsAdapter=new ChatsAdapter(chatsList, this, this);
        chatsRecyclerView.setAdapter(chatsAdapter);

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
                intent = new Intent(ChatsListActivity.this, Home_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                intent = new Intent(ChatsListActivity.this, UserProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_courses:
                intent = new Intent(ChatsListActivity.this, EditCoursesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_session:
                intent = new Intent(ChatsListActivity.this, Session_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_chatslist:
                intent = new Intent(ChatsListActivity.this, ChatsListActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                intent = new Intent(ChatsListActivity.this, Bindr.class);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* End Navigation Stuff */
    //Navigate to chatbox when a chat is clicked
    @Override
    public void onChatClick(int position) {

        Intent intent=new Intent(ChatsListActivity.this, ChatboxActivity.class);
        startActivity(intent);
    }

    @Override
    public void OnMatchIconClick(int position) {
        Intent intent=new Intent(ChatsListActivity.this, ChatboxActivity.class);
        startActivity(intent);

    }
}
