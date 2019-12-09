package com.study.bindr;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.material.navigation.NavigationView;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import model.Chat;
import model.Course;
import model.Student;

public class ChatsListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ChatsAdapter.OnChatListener,
        MatchedStudentAdapter.OnMatchIconListener, AdapterView.OnItemSelectedListener {
    //Need this for our drawer layout
    private DrawerLayout drawer;

    //Drop down to select which course to search by
    private Spinner searchByDropDown;
    //Search by user name
    private SearchView searchView;

    //Adapter used for displaying the lists of matches(not chatting) and chats
    private RecyclerView matchedStudentsRecyclerView;
    private MatchedStudentAdapter matchedStudentAdapter;

    private RecyclerView chatsRecyclerView;
    private ChatsAdapter chatsAdapter;

    //Stores matches and chats
    private ArrayList<Student> matchedStudentsList = new ArrayList<>();
    private ArrayList<Chat> chatsList = new ArrayList<>();

    //Stores list of courses that the current user is in. Will be used for searching students by course.
    private ArrayList<Course> coursesList=new ArrayList<>();

    //The current user
    private Student currentUser=BindrController.getCurrentUser();


    /**
     * displays activity, runs code for startup.
     * Creates navigation bar, dropdowns and matches/chats lists
     * @param savedInstanceState -bundle passed by previous activity
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

        /* Set up the recyclerviews and adapters */
        matchedStudentsRecyclerView = findViewById(R.id.matchedRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        matchedStudentsRecyclerView.setLayoutManager(layoutManager);

        chatsRecyclerView = findViewById(R.id.chatsRecyclerView);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatsRecyclerView.setLayoutManager(layoutManager);


        /* Setup the drop down for search filter */
        searchByDropDown=findViewById(R.id.searchByDropDown);
        populateCourseDropDown();

        searchView = findViewById(R.id.searchView);

        //Listens for changes in search input and displays the filtered results
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override //Filter the matches and chats list whenever text changes
            public boolean onQueryTextChange(String newText) {
                matchedStudentAdapter.getFilter().filter(newText);
                chatsAdapter.getFilter().filter(newText);
                return false;
            }
        });
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
     * @param position the chats list position that the selected chatting student is in
     */
    @Override
    public void onChatClick(int position) {
        //Selecting a chatting student will return a Chat object, which already contains information about the chat room and chatting student
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
     * @param position the matched students list position that the selected student is in
     */
    @Override
    public void OnMatchIconClick(int position) {
        //Selecting a matched student will return the Student object of that student
        Student matchedStudent=matchedStudentsList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("type", "matched");
        bundle.putSerializable("Student", matchedStudent);
        Intent intent=new Intent(ChatsListActivity.this, ChatboxActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);

    }

    /**
     * Finds ALL the matches for the current user from the database and displays them using the adapter
     */
    private void populateMatches(){
        currentUser.getMatchedNotChatting(new DatabaseCallBack<List<String>>() {
            @Override
            public void onCallback(List<String> studentIDs) {
                setMatchedStudentAdapter(studentIDs);
            }
        });

    }
    /**
     * Finds the ALL chats for the current user from the database and displays them using the adapter
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

    /**
     * Sets the matched adapter with the matched list.
     * @param studentIDs
     */
    private void setMatchedStudentAdapter(List<String> studentIDs){
        //Get the full name of each student id so adapter can do filtering
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
     * Sets the chats adapter with the chats list.
     * @param studentIDs a list of chatting student IDs that the
     */
    private void setChatsAdapter(List<String> studentIDs){

        //Get the full name of each student id so adapter can do filtering
        DatabaseUtility.getFullNameList(new DatabaseCallBack<List<String>>() {
            @Override
            public void onCallback(List<String> fullNamesList) {
                chatsAdapter=new ChatsAdapter(chatsList,fullNamesList,currentUser.getId(), ChatsListActivity.this, ChatsListActivity.this);
                chatsRecyclerView.setAdapter(chatsAdapter);
            }
        },studentIDs);

    }

    /**
     * Filters and displays the chats and matches lists according to the selected courses in drop down.
     * @param parent The AdapterView where the selection happened
     * @param view The view within the AdapterView that was clicked
     * @param pos The position of the view in the adapter
     * @param id The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        //Clear search when user selects another course to search by
        searchView.setIconified(true);
            searchView.clearFocus();

        String selection = (String)parent.getItemAtPosition(pos);

        //Display all
        if (selection.equals("All")){
            populateChats();
            populateMatches();
        }
        else {
            //Get all the current user's matched not chatting students
            currentUser.getMatchedNotChatting(new DatabaseCallBack<List<String>>() {

                @Override
                public void onCallback(List<String> studentIDs) {
                    //find the course in the courselist
                    Course course = getCourseFromName(selection);
                    //Get only the students in this course
                    DatabaseUtility.getOnlyStudentsInCourse(new DatabaseCallBack<List<String>>() {
                        @Override
                        public void onCallback(List<String> filteredStudentIDs) {
                            //reset the matches adapter
                            setMatchedStudentAdapter(filteredStudentIDs);

                        }
                    }, studentIDs, course);
                }
            });
            //Get all the current user's chats
            currentUser.getChatRooms(new DatabaseCallBack<List<Document>>() {
                @Override
                public void onCallback(List<Document> items) {
                    //find the course in the courselist
                    Course course = getCourseFromName(selection);

                    //Get the list of chatting student IDs
                    List<String> studentIDs=new ArrayList<>();

                    for (int i=0; i<items.size(); i++){
                        String studentID=items.get(i).get("student").toString();
                        studentIDs.add(studentID);
                    }
                    //Get only the students in this course
                    DatabaseUtility.getOnlyStudentsInCourse(new DatabaseCallBack<List<String>>() {
                        @Override
                        public void onCallback(List<String> filteredStudentIDs) {
                            //Get the chat rooms from the filtered students
                            currentUser.getChatRoomsFromStudents(new DatabaseCallBack<List<Document>>() {
                                @Override
                                public void onCallback(List<Document> chatRooms) {
                                    List<String >ids=convertToChatList(chatRooms);
                                    //reset the chats adapter
                                    setChatsAdapter(ids);
                                }
                            },filteredStudentIDs);
                        }
                    }, studentIDs, course);
                }
            });
        }
    }

    /**
     * Nothing should occur when the selection disappears from this view.
     * @param parent The AdapterView where the selection happened
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //do nothing
    }


    /**
     * Populates the searchByDropDown Spinner with the courses that the current user has.
     * Stores the courses in coursesList
     */
    public void populateCourseDropDown(){
        currentUser.getCourses(new DatabaseCallBack<List<Document>>() {
            @Override
            public void onCallback(List<Document> courses) {
                //User can search from All students or search students in selected courses
                List<String> searchOptions = new ArrayList<String>();
                searchOptions.add("All");

                //parse course documents
                for (int i=0; i<courses.size();i++){
                    Document courseDoc=courses.get(i);
                    String courseName=courseDoc.getString("courseName");
                    String schoolID=courseDoc.getString("schoolID");
                    String departmentID=courseDoc.getString("departmentID");
                    String courseID=courseDoc.getString("courseID");
                    searchOptions.add(courseName);
                    Course course=new Course(schoolID, departmentID, courseID, courseName);

                    coursesList.add(course);
                }

                //set the dropdown with the course names
                ArrayAdapter<String> searchOptionsAdapter = new ArrayAdapter<String>(ChatsListActivity.this,
                        android.R.layout.simple_spinner_item, searchOptions);

                searchOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                searchByDropDown.setAdapter(searchOptionsAdapter);
                searchByDropDown.setOnItemSelectedListener(ChatsListActivity.this);
            }
        });

    }

    /**
     * Searches for the course object in the courses list given a course name.
     * @param courseName
     * @return the course object of the given course name
     */
    private Course getCourseFromName(String courseName){
        Course course = null;
        for (int i = 0; i < coursesList.size(); i++) {
            if (coursesList.get(i).getCourseName().equals(courseName)) {
                course = coursesList.get(i);
                break;
            }
        }
        return course;
    }

    /**
     * Converts a list of chat documents into a list of chat objects and stores them in chatsList\
     * Returns a list of the chatting student's ids from each chat
     * @param items list of chat documents.
     * @return
     */
    private List<String> convertToChatList(List<Document> items){
        //reset the chats list
        chatsList.clear();
        List<String> studentIDs=new ArrayList<>();
        //repopulate chats list
        for (int i=0; i<items.size(); i++){
            String chatRoom=items.get(i).getString("room");
            String studentID=items.get(i).get("student").toString();
            Chat chat=new Chat(chatRoom,studentID);
            chatsList.add(chat);
            studentIDs.add(studentID);
        }
        return studentIDs;

    }
}
