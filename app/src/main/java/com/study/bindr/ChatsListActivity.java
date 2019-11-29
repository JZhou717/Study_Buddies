package com.study.bindr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteFindOptions;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import model.Chat;
import model.Student;

public class ChatsListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ChatsAdapter.OnChatListener,
        MatchedStudentAdapter.OnMatchIconListener {
    //Need this for our drawer layout
    private DrawerLayout drawer;

    private RecyclerView matchedStudentsRecyclerView;
    private RecyclerView.Adapter matchedStudentAdapter;

    private RecyclerView chatsRecyclerView;
    private RecyclerView.Adapter chatsAdapter;

    private ArrayList<Student> matchedStudentsList = new ArrayList<>();
    private ArrayList<Chat> chatsList = new ArrayList<>();


    //CURRENT STUDENT TEST
    private String id="5ddc5d142b665e671c7ff7bd";
    private Student me=new Student(id);

    /**
     * Populates and displays the matched students list and chats list.
     * Sets up the search filters
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chats_list);

        /* Start Navigation Stuff */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Chats");

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

        /* Start setup drop down for search filter */
        final Spinner searchByDropDown=findViewById(R.id.searchByDropDown);
        List<String> searchOptions = new ArrayList<String>();
        searchOptions.add("User");
        searchOptions.add("Course Name");

        ArrayAdapter<String> searchOptionsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, searchOptions);

        searchOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        searchByDropDown.setAdapter(searchOptionsAdapter);
        /* End setup drop down for search filter */

        /* Start setup adapters and populates matched and chatting students */
        matchedStudentsRecyclerView = findViewById(R.id.matchedRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        matchedStudentsRecyclerView.setLayoutManager(layoutManager);

        chatsRecyclerView = findViewById(R.id.chatsRecyclerView);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatsRecyclerView.setLayoutManager(layoutManager);


        populateMatches();
        populateChats();
        /* End setup adapters and populates matched and chatting students */

    }

    /**
     * Navbar closes on activity change
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Navigates to selected screen
     */
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


    /**
     * Navigates to the chatbox screen between current user and the selected student.
     */
    @Override
    public void onChatClick(int position) {
        Chat chat=chatsList.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Chat", chat);
        bundle.putString("type", "chat");
        Intent intent=new Intent(ChatsListActivity.this, ChatboxActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    /**
     * Navigates to a new chatbox screen between current user and the selected student.
     */
    @Override
    public void OnMatchIconClick(int position) {
        Student matchedStudent=matchedStudentsList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("type", "matched");
        bundle.putSerializable("Student", matchedStudent);

        Intent intent=new Intent(ChatsListActivity.this, ChatboxActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);

    }

    /**
     * Finds the matches for the current user from the database and displays them using the adapter
     */
    private void populateMatches(){
        me.getMatched(new DatabaseCallBack<List<String>>() {
            @Override
            public void onCallback(List<String> items) {
                //populate and set the adapter
                for (int i=0; i<items.size(); i++){
                    Student matchedStudent=new Student(items.get(i).toString());
                    matchedStudentsList.add(matchedStudent);
                }
                matchedStudentAdapter=new MatchedStudentAdapter(matchedStudentsList, ChatsListActivity.this, ChatsListActivity.this);
                matchedStudentsRecyclerView.setAdapter(matchedStudentAdapter);
            }
        });

    }
    /**
     * Finds the chats for the current user from the database and displays them using the adapter
     */
    private void populateChats(){
        me.getChatRooms(new DatabaseCallBack<List<String>>() {
            @Override
            public void onCallback(List<String> items) {
                //populate and set the adapter
                for (int i=0; i<items.size(); i++){
                    Chat chatRoom=new Chat(items.get(i).toString());
                    chatsList.add(chatRoom);
                }
                chatsAdapter=new ChatsAdapter(chatsList,id, ChatsListActivity.this, ChatsListActivity.this);
                chatsRecyclerView.setAdapter(chatsAdapter);
            }
        });

    }

}
