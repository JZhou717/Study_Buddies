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
import android.widget.ListView;

import com.google.android.material.navigation.NavigationView;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import model.Course;
import model.Student;

public class Home_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Need this for our drawer layout
    private DrawerLayout drawer;
    private ListView courseListView;
    private CourseAdapter courseAdapter;


    private ArrayList<Course> courses = new ArrayList<>();

    //CURRENT STUDENT TEST
    private String id = "5ddc5d142b665e671c7ff7bd";
    private Student me = new Student(id);


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


        //link xml component
        courseListView = findViewById(R.id.courseListView);
        populateCourses();
    }



    @Override
    public void onStart(){
        super.onStart();
        //TO GET THE COURSES YOU HAVE TO USE THE CALLBACK LIKE THE ONES IN THE LOGIN METHOD ON BINDR
        //CHECK FEIYING'S IMPLEMENTATION OF GETCHATS TO SEE HOW SHE USED IT

        //List<Course> courses = student.getCourses();
        //courseAdapter = new CourseAdapter(this,courses);
        //courseListView.setAdapter(courseAdapter);
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

    private void populateCourses(){
        me.getCourses(new DatabaseCallBack<List<Document>>() {
            @Override
            public void onCallback(List<Document> items) {
                for (int i = 0; i < items.size(); i++){
                    String schoolID = items.get(i).get("schoolID").toString();
                    String departmentID = items.get(i).get("departmentID").toString();
                    String courseID = items.get(i).get("courseID").toString();
                    String courseName = schoolID + ":" + departmentID + ":" + courseID;
                    Course course = new Course(schoolID, departmentID, courseID, courseName);
                    courses.add(course);
                }
                courseAdapter = new CourseAdapter(Home_Activity.this,courses);
                courseListView.setAdapter(courseAdapter);
            }
        });
    }
}
