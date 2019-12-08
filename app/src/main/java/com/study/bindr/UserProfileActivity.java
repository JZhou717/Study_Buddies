package com.study.bindr;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.bson.Document;

import java.util.List;

import model.Course;
import model.Student;

public class UserProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Need this for our drawer layout
    private DrawerLayout drawer;

    private ImageView editProfileImageView;
    private ImageView profilePicImageView;
    private EditText nameEditText;
    private EditText coursesEditText;
    private EditText gpaEditText;
    private EditText bioEditText;
    private EditText interestsEditText;
    private EditText emailEditText;
    private Button confirmButton;
    private Button cancelButton;
    private Button deleteButton;
    private boolean isInEditMode;
    private Switch activeSwitch;

    //TODO: STORE PREVIOUS PROFILE PICTURE
    //TODO: user status
    private final int KEY_LISTENER_INDEX = 0;
    private final int TEXT_INDEX = 1;
    private Student displayedStudent;
    private Student me = BindrController.getCurrentUser();

    private static final String TAG = "UserProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_user_profile);
        Log.d(TAG, "Starting onCreate() for UserProfileActivity");

        /* Start Navigation Stuff */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        Log.d(TAG, "Started Navigation");


        //Change the view to the proper screen
        drawer = findViewById(R.id.profile_screen);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Change the selection to the proper screen
        navigationView.setCheckedItem(R.id.nav_profile);
        /* End Navigation Stuff */
        Log.d(TAG, "Ended navigation");
        String displayedStudentID = getIntent().getStringExtra("STUDENT_ID");
        displayedStudent = new Student(displayedStudentID);
        isInEditMode = false;
        final boolean displayedStudentIsMe = displayedStudentID.equals(me.getId());

        editProfileImageView = (ImageView)findViewById(R.id.imageViewEditProfile);

        profilePicImageView = (ImageView)findViewById(R.id.imageViewProfilePic);
        //TODO: RETRIEVE PROFILE PIC

        activeSwitch = (Switch)findViewById(R.id.activeSwitch);

        nameEditText = (EditText)findViewById(R.id.editTextName);
        displayedStudent.getFullName(items -> nameEditText.setText(items));
        toggleEditable(nameEditText);

        coursesEditText = (EditText)findViewById(R.id.editTextCourses);
        toggleEditable(coursesEditText);
        displayedStudent.getCourses(items -> {
            String coursesListAsString = "";
            if(items.size() > 0) {
                for (int i = 0; i < items.size() - 1; i++) {
                    coursesListAsString += ", " + items.get(i).getString("courseName");
                }
                coursesListAsString += items.get(items.size() - 1).getString("courseName");
            }
            coursesEditText.setText(coursesListAsString);
        });

        gpaEditText = (EditText)findViewById(R.id.editTextGPA);
        displayedStudent.getGPA(items -> gpaEditText.setText(String.format("%.2f", items)));

        bioEditText = (EditText)findViewById(R.id.editTextBio);
        displayedStudent.getBio(items -> bioEditText.setText(items));

        interestsEditText = (EditText)findViewById(R.id.editTextInterests);
        displayedStudent.getInterests(items -> interestsEditText.setText(items));

        confirmButton = (Button)findViewById(R.id.buttonConfirm);
        confirmButton.setVisibility(View.INVISIBLE);

        cancelButton = (Button)findViewById(R.id.buttonCancel);
        cancelButton.setVisibility(View.INVISIBLE);

        deleteButton = (Button)findViewById(R.id.buttonDeleteAccount);

        emailEditText = (EditText)findViewById(R.id.editTextEmail);
        exitEditMode();

        if(!displayedStudentIsMe){
            editProfileImageView.setVisibility(View.INVISIBLE);
            emailEditText.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
            (findViewById(R.id.textViewEmailLabel)).setVisibility(View.INVISIBLE);
            activeSwitch.setClickable(false);
        }
        else{
            displayedStudent.getEmail(items -> emailEditText.setText(items));
        }
    }

    public void onEditProfileClicked(View v){
        editProfileImageView.setVisibility(View.INVISIBLE);
        confirmButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        isInEditMode = true;
        toggleEditable(nameEditText);
        toggleEditable(gpaEditText);
        toggleEditable(bioEditText);
        toggleEditable(interestsEditText);
        toggleEditable(emailEditText);
        //do NOT toggleEditable(coursesEditText)
        //only edit courses through the edit courses screen
    }

    public void onProfilePicClicked(View v){
        if(isInEditMode){
            //TODO: IMPLEMENT
            //Should allow user to select a new profile picture
        }
        //else do nothing
    }

    public void onConfirmClicked(View v){
        exitEditMode();
        displayedStudent.editName(nameEditText.getText().toString(), items -> {});
        displayedStudent.editGPA(Double.parseDouble(gpaEditText.getText().toString()),
                items -> {});
        displayedStudent.editBio(bioEditText.getText().toString(), items -> {});
        displayedStudent.editEmail(bioEditText.getText().toString(), items -> {});
        displayedStudent.editInterests(interestsEditText.getText().toString());
    }

    private void exitEditMode(){
        isInEditMode = false;
        toggleEditable(nameEditText);
        toggleEditable(gpaEditText);
        toggleEditable(bioEditText);
        toggleEditable(interestsEditText);
        toggleEditable(emailEditText);
        confirmButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        editProfileImageView.setVisibility(View.VISIBLE);
    }

    public void onCancelClicked(View v){
        exitEditMode();
        restoreValues();
    }

    private void restoreValues(){
        nameEditText.setText((String)nameEditText.getTag(TEXT_INDEX));
        gpaEditText.setText((String)gpaEditText.getTag(TEXT_INDEX));
        bioEditText.setText((String)bioEditText.getTag(TEXT_INDEX));
        interestsEditText.setText((String)interestsEditText.getTag(TEXT_INDEX));
        emailEditText.setText((String)emailEditText.getTag(TEXT_INDEX));
    }

    public void onDeleteClicked(View v){
        //for each s in me's matches,
        //    pull me from s' matches
        //for each c in me's courses,
        //    pull me from c's students
        //delete me's student document
        me.getMatched(items -> {
            for(int i=0; i<items.size(); i++){
                Student s = new Student(items.get(i));
                s.removeMatchedStudent(me.getId());
            }
        });
        me.getCourses(items -> {
            for(int i=0; i<items.size(); i++){
                String schoolID = items.get(i).get("schoolID").toString();
                String departmentID = items.get(i).get("departmentID").toString();
                String courseID = items.get(i).get("courseID").toString();
                new Course(schoolID, departmentID, courseID, "")
                        .removeStudentFromThisCourseInDatabase(me.getId());
            }
        });
        me.deleteAccount();
    }

    private void toggleEditable(EditText et){
        if(isInEditMode){
            et.setTag(TEXT_INDEX, et.getText().toString());
            et.setKeyListener((KeyListener)(et.getTag(KEY_LISTENER_INDEX)));
        }
        else{
            et.setTag(KEY_LISTENER_INDEX, et.getKeyListener());
            et.setKeyListener(null);
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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {
            case R.id.nav_home:
                intent = new Intent(UserProfileActivity.this, Home_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                intent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_courses:
                intent = new Intent(UserProfileActivity.this, EditCoursesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_session:
                intent = new Intent(UserProfileActivity.this, Session_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_chatslist:
                intent = new Intent(UserProfileActivity.this, ChatsListActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                intent = new Intent(UserProfileActivity.this, Bindr.class);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* End Navigation Stuff */
}
