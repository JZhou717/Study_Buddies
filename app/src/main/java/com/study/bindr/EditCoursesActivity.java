package com.study.bindr;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import model.Course;

public class EditCoursesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Need this for our drawer layout
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_courses);

        /* Start Navigation Stuff */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Courses");

        //Change the view to the proper screen
        drawer = findViewById(R.id.courses_screen);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Change this to the proper page
        navigationView.setCheckedItem(R.id.nav_courses);
        /* End Navigation Stuff */
    }

    public void addCourse(View view){
        int schoolNum = Integer
                .parseInt(((EditText)findViewById(R.id.editSchoolNum)).getText().toString());
        int deptNum = Integer
                .parseInt(((EditText)findViewById(R.id.editDeptNum)).getText().toString());
        int courseNum = Integer
                .parseInt(((EditText)findViewById(R.id.editCourseNum)).getText().toString());
        String courseName = ((EditText)findViewById(R.id.editCourseName)).getText().toString();
        Course courseToBeAdded = new Course(schoolNum, deptNum, courseNum, courseName);
        courseToBeAdded.addStudentToThisCourseInDatabase(BindrController.getCurrentUser().getId());
        BindrController.getCurrentUser().addCourse(courseToBeAdded);
        //TODO: Make sure course is not already in the table, use Course.equals()
        //create table row tr
        //tr will have course name, courseID=schoolNum:deptNum:courseNum, remove button
        //remove button should have tag that is tr's index
        //add tr to the tablelayout
        TableLayout table = (TableLayout)findViewById(R.id.tableCoursesAdded);

        TableRow tr = new TableRow(this);
        tr.setTag(courseToBeAdded);

        TextView courseNameTextView = new TextView(this);
        TextView courseIDTextView = new TextView(this);
        Button removeButton = new Button(this);

        courseNameTextView.setText(courseName);
        courseIDTextView.setText(String.format("%d:%d:%d", schoolNum, deptNum, courseNum));
        removeButton.setTag(courseToBeAdded);

        tr.addView(courseNameTextView);
        tr.addView(courseIDTextView);
        tr.addView(removeButton);
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

    public void done(View view){
        Intent intent = new Intent(EditCoursesActivity.this, Home_Activity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {
            case R.id.nav_home:
                intent = new Intent(EditCoursesActivity.this, Home_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                intent = new Intent(EditCoursesActivity.this, UserProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_courses:
                intent = new Intent(EditCoursesActivity.this, EditCoursesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_session:
                intent = new Intent(EditCoursesActivity.this, Session_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_chatslist:
                intent = new Intent(EditCoursesActivity.this, ChatsListActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                intent = new Intent(EditCoursesActivity.this, Bindr.class);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* End Navigation Stuff */
}
