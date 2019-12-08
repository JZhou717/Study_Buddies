package com.study.bindr;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
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
import model.Course;
import model.Student;

public class ChatsListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ChatsAdapter.OnChatListener,
        MatchedStudentAdapter.OnMatchIconListener, AdapterView.OnItemSelectedListener {
    //Need this for our drawer layout
    private DrawerLayout drawer;
    private Spinner searchByDropDown;
    private RecyclerView matchedStudentsRecyclerView;
    private MatchedStudentAdapter matchedStudentAdapter;

    private RecyclerView chatsRecyclerView;
    private ChatsAdapter chatsAdapter;
    private SearchView searchView;
    private ArrayList<Student> matchedStudentsList = new ArrayList<>();
    private ArrayList<Chat> chatsList = new ArrayList<>();

    private ArrayList<Course> coursesList=new ArrayList<>();
    private Student currentUser=BindrController.getCurrentUser();

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
        searchByDropDown=findViewById(R.id.searchByDropDown);

        /* End setup drop down for search filter */

        /* Start setup adapters and populates matched and chatting students */
        matchedStudentsRecyclerView = findViewById(R.id.matchedRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        matchedStudentsRecyclerView.setLayoutManager(layoutManager);

        chatsRecyclerView = findViewById(R.id.chatsRecyclerView);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatsRecyclerView.setLayoutManager(layoutManager);


        searchView = findViewById(R.id.searchView);
        populateCourseDropDown();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                matchedStudentAdapter.getFilter().filter(newText);
                chatsAdapter.getFilter().filter(newText);
                return false;
            }
        });

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
        currentUser.getMatchedNotChatting(new DatabaseCallBack<List<String>>() {
            @Override
            public void onCallback(List<String> studentIDs) {
                setMatchedStudentAdapter(studentIDs);
            }
        });

    }
    private void setMatchedStudentAdapter(List<String> studentIDs){

        DatabaseUtility.getFullNameList(new DatabaseCallBack<List<String>>() {
            @Override
            public void onCallback(List<String> fullNamesList) {
                if (fullNamesList==null || fullNamesList.size()==0 ){
                    System.out.println("EMPTY");
                }
                matchedStudentsList.clear();
                //populate and set the adapter
                for (int i=0; i<studentIDs.size(); i++){
                    Student matchedStudent=new Student(studentIDs.get(i).toString());
                    matchedStudentsList.add(matchedStudent);
                }
                matchedStudentAdapter=new MatchedStudentAdapter(matchedStudentsList, fullNamesList,ChatsListActivity.this, ChatsListActivity.this);
                matchedStudentsRecyclerView.setAdapter(matchedStudentAdapter);
            }
        },studentIDs);

    }
    /**
     * Finds the chats for the current user from the database and displays them using the adapter
     */
    private void populateChats(){

        currentUser.getChatRooms(new DatabaseCallBack<List<Document>>() {
            @Override
            public void onCallback(List<Document> items) {
                List<String> studentIDs=convertToChatList(items);
                setChatsAdapter(studentIDs);

            }
        });

    }
    private List<String>  convertToChatList(List<Document> items){
        chatsList.clear();
        List<String> studentIDs=new ArrayList<>();
        //populate and set the adapter
        for (int i=0; i<items.size(); i++){
            String chatRoom=items.get(i).getString("room");
            String studentID=items.get(i).get("student").toString();
            Chat chat=new Chat(chatRoom,studentID);
            chatsList.add(chat);
            studentIDs.add(studentID);
        }
        return studentIDs;

    }

    private void setChatsAdapter(List<String> studentIDs){

        DatabaseUtility.getFullNameList(new DatabaseCallBack<List<String>>() {
            @Override
            public void onCallback(List<String> fullNamesList) {
                chatsAdapter=new ChatsAdapter(chatsList,fullNamesList,currentUser.getId(), ChatsListActivity.this, ChatsListActivity.this);
                chatsRecyclerView.setAdapter(chatsAdapter);
            }
        },studentIDs);

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        searchView.setIconified(true);
            searchView.clearFocus();

        String selection = (String)parent.getItemAtPosition(pos);
        System.out.println("SELECTION "+selection);
        if (selection.equals("All")){
            populateChats();
            populateMatches();
        }
        else {
            currentUser.getMatchedNotChatting(new DatabaseCallBack<List<String>>() {

                @Override
                public void onCallback(List<String> studentIDs) {
                    //find course
                    Course course = null;
                    for (int i = 0; i < coursesList.size(); i++) {
                        if (coursesList.get(i).getCourseName().equals(selection)) {
                            course = coursesList.get(i);
                            break;
                        }
                    }
                    DatabaseUtility.getOnlyStudentsInCourse(new DatabaseCallBack<List<String>>() {
                        @Override
                        public void onCallback(List<String> filteredStudentIDs) {
                            setMatchedStudentAdapter(filteredStudentIDs);

                        }
                    }, studentIDs, course);
                }
            });

            currentUser.getChatRooms(new DatabaseCallBack<List<Document>>() {
                @Override
                public void onCallback(List<Document> items) {
                    //find course
                    Course course = null;
                    for (int i = 0; i < coursesList.size(); i++) {
                        if (coursesList.get(i).getCourseName().equals(selection)) {
                            course = coursesList.get(i);
                            break;
                        }
                    }
                    List<String> studentIDs=new ArrayList<>();
                    //populate and set the adapter
                    for (int i=0; i<items.size(); i++){
                        String chatRoom=items.get(i).getString("room");
                        String studentID=items.get(i).get("student").toString();
                        studentIDs.add(studentID);
                    }

                    DatabaseUtility.getOnlyStudentsInCourse(new DatabaseCallBack<List<String>>() {
                        @Override
                        public void onCallback(List<String> filteredStudentIDs) {
                            System.out.println("FILTERED "+filteredStudentIDs);
                            currentUser.getChatRoomsFromStudents(new DatabaseCallBack<List<Document>>() {
                                @Override
                                public void onCallback(List<Document> chatRooms) {
                                    List<String >ids=convertToChatList(chatRooms);
                                    setChatsAdapter(ids);
                                }
                            },filteredStudentIDs);


                        }
                    }, studentIDs, course);
                }
            });

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //do nothing
    }


    public void populateCourseDropDown(){
        currentUser.getCourses(new DatabaseCallBack<List<Document>>() {
            @Override
            public void onCallback(List<Document> courses) {
                List<String> searchOptions = new ArrayList<String>();
                searchOptions.add("All");
                for (int i=0; i<courses.size();i++){
                    String courseName=courses.get(i).getString("courseName");
                    String schoolID=courses.get(i).getString("schoolID");
                    String departmentID=courses.get(i).getString("departmentID");;
                    String courseID=courses.get(i).getString("courseID");

                    searchOptions.add(courseName);
                    Course course=new Course(schoolID, departmentID, courseID, courseName);
                    coursesList.add(course);

                }

                ArrayAdapter<String> searchOptionsAdapter = new ArrayAdapter<String>(ChatsListActivity.this,
                        android.R.layout.simple_spinner_item, searchOptions);

                searchOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                searchByDropDown.setAdapter(searchOptionsAdapter);
                searchByDropDown.setOnItemSelectedListener(ChatsListActivity.this);
            }
        });

    }
}
