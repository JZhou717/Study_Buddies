package com.study.bindr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.google.android.material.snackbar.Snackbar;

import org.bson.Document;

import java.util.List;

import model.Course;
import model.Student;

public class EditCoursesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Need this for our drawer layout
    private DrawerLayout drawer;

    //private Student me = BindrController.getCurrentUser();

    //TODO: DELETE THESE TWO STATEMENTS AND UNCOMMENT ABOVE STATEMENT
    private String id = "5ddb3fd5c3de9037b0b2ced6";
    private Student me = new Student(id);

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
        Log.d("EditCoursesOnCreate: ", "Populating courses...");
        //Populate courses:
        me.getCourses(new DatabaseCallBack<List<Document>>() {
            @Override
            public void onCallback(List<Document> items) {
                for (int i = 0; i < items.size(); i++){
                    String schoolID = items.get(i).get("schoolID").toString();
                    String departmentID = items.get(i).get("departmentID").toString();
                    String courseID = items.get(i).get("courseID").toString();
                    String courseName = items.get(i).get("courseName").toString();
                    Log.d("EditCoursesOnCreate", String.format("Adding %s:%s:%s",
                            schoolID, departmentID, courseID));
                    addTableRowForCourse(new Course(schoolID, departmentID, courseID, courseName));
                }
            }
        });
    }

    private void addTableRowForCourse(Course course){
        //create table row tr
        //tr will have course name, courseID=schoolNum:deptNum:courseNum, remove button
        //remove button should have tag that is tr's index
        //add tr to the tablelayout
        TableLayout table = (TableLayout)findViewById(R.id.tableCoursesAdded);

        TableRow tr = new TableRow(this);
        tr.setTag(course);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));


        TextView courseNameTextView = new TextView(this);
        TextView courseIDTextView = new TextView(this);
        Button removeButton = new Button(this);

        String courseNameToBeDisplayed = course.getCourseName();
        //truncate courseName if needed
        if(courseNameToBeDisplayed.length() > 14){
            courseNameToBeDisplayed = courseNameToBeDisplayed.substring(0, 12) + "...";
        }
        courseNameTextView.setText(courseNameToBeDisplayed);
        courseIDTextView.setText(String.format("%s:%s:%s",
                course.getSchoolID(), course.getDepartmentID(), course.getCourseID()));
        removeButton.setTag(course);
        removeButton.setText("Remove");
        removeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                removeCourse(course);
            }
        });

        tr.addView(courseNameTextView);
        tr.addView(courseIDTextView);
        tr.addView(removeButton);

        table.addView(tr);
    }

    public void addCourse(View view){
        String schoolNum = ((EditText)findViewById(R.id.editSchoolNum)).getText().toString();
        String deptNum = ((EditText)findViewById(R.id.editDeptNum)).getText().toString();
        String courseNum = ((EditText)findViewById(R.id.editCourseNum)).getText().toString();
        String courseName = ((EditText)findViewById(R.id.editCourseName)).getText().toString();

        TableLayout table = (TableLayout)findViewById(R.id.tableCoursesAdded);
        Course courseToBeAdded = new Course(schoolNum, deptNum, courseNum, courseName);

        //Force withdraw keyboard:
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        //Make sure course has not already been added:
        for(int i=1, j=table.getChildCount(); i<j; i++){
            if(table.getChildAt(i).getTag() == null) {
                Log.d("addCourse", String.format("Row %d: null tag", i));
                continue;
            }
            if(((Course)(table.getChildAt(i).getTag())).equals(courseToBeAdded)){
                Log.d("addCourse", String.format("Row %d: duplicate", i));
                Snackbar courseAlreadyExistsSnackBar = Snackbar.make(findViewById(R.id.addButton),
                        "Course already added", Snackbar.LENGTH_SHORT);
                courseAlreadyExistsSnackBar.show();
                return;
            }
           Log.d("addCourse",
                  String.format("Row %d: course does not equal courseToBeAdded", i));
        }

        courseToBeAdded.addStudentToThisCourseInDatabase(me.getId());
        me.addCourse(courseToBeAdded);
        addTableRowForCourse(courseToBeAdded);
    }

    private void removeCourse(Course courseToBeRemoved){
        if(courseToBeRemoved==null){
            Log.d("EditCourses", "courseToBeRemoved is null");
        }
        //Remove from user
        me.removeCourse(courseToBeRemoved);
        //Remove user from course
        courseToBeRemoved.removeStudentFromThisCourseInDatabase(me.getId());
        //Remove from table
        TableLayout table = (TableLayout)findViewById(R.id.tableCoursesAdded);
        for(int i=1, j=table.getChildCount(); i<j; i++){
            if(table.getChildAt(i).getTag() == null)
                continue;
            if(((Course)(table.getChildAt(i).getTag())).equals(courseToBeRemoved)){
                table.removeViewAt(i);
                return;
            }
        }
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
