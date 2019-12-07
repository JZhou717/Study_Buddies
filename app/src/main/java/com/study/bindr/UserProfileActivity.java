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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

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
    private Button confirmButton;
    private Button cancelButton;
    private Button deleteOrBlockButton;
    private boolean isInEditMode;
    //TODO: email

    //TODO: STORE PREVIOUS PROFILE PICTURE
    //TODO: user status
    private final int KEY_LISTENER_INDEX = 0;
    private final int TEXT_INDEX = 1;
    private Student displayedStudent;
    private Student me;

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
        toggleEditable(gpaEditText);
        displayedStudent.getGPA(items -> gpaEditText.setText(String.format("%.2f", items)));

        bioEditText = (EditText)findViewById(R.id.editTextBio);
        toggleEditable(bioEditText);
        displayedStudent.getBio(items -> bioEditText.setText(items));

        interestsEditText = (EditText)findViewById(R.id.editTextInterests);
        toggleEditable(interestsEditText);
        //TODO: uncomment this:
        //displayedStudent.getInterests(items -> interestsEditText.setText(items));

        //TODO: email

        confirmButton = (Button)findViewById(R.id.buttonConfirm);
        confirmButton.setVisibility(View.INVISIBLE);

        cancelButton = (Button)findViewById(R.id.buttonCancel);
        cancelButton.setVisibility(View.INVISIBLE);

        deleteOrBlockButton = (Button)findViewById(R.id.buttonDeleteOrBlockAccount);

        if(!displayedStudentIsMe){
            editProfileImageView.setVisibility(View.INVISIBLE);
            deleteOrBlockButton.setText("Block User");
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
        //TODO: email
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
        toggleEditable(nameEditText);
        toggleEditable(gpaEditText);
        toggleEditable(bioEditText);
        toggleEditable(interestsEditText);
        displayedStudent.editName(nameEditText.getText().toString(), items -> {});
        displayedStudent.editGPA(Double.parseDouble(gpaEditText.getText().toString()),
                items -> {});
        displayedStudent.editBio(bioEditText.getText().toString(), items -> {});
        //TODO: Uncomment
        //displayedStudent.editInterests(interestsEditText.getText().toString(), items -> {});
        //TODO: email
    }

    public void onCancelClicked(View v){
        toggleEditable(nameEditText);
        toggleEditable(gpaEditText);
        toggleEditable(bioEditText);
        toggleEditable(interestsEditText);
        //TODO: email
        restoreValues();
    }

    private void restoreValues(){
        nameEditText.setText((String)nameEditText.getTag(TEXT_INDEX));
        gpaEditText.setText((String)gpaEditText.getTag(TEXT_INDEX));
        bioEditText.setText((String)bioEditText.getTag(TEXT_INDEX));
        interestsEditText.setText((String)interestsEditText.getTag(TEXT_INDEX));
        //TODO: email
    }

    public void onDeleteOrBlockClicked(View v){

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
