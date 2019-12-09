package com.study.bindr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.KeyListener;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    private RatingBar ratingBar;
    Uri imageUri;

    //TODO: STORE PREVIOUS PROFILE PICTURE
    private Student displayedStudent;
    private Student me = BindrController.getCurrentUser();
    private static final int PICK_IMAGE = 100;

    private static final String TAG = "UserProfileActivity";

    DialogInterface.OnClickListener deleteClickListener;

    /**
     * initialization of activity
     * @param savedInstanceState - data of saved instance (passes "STUDENT_ID")
     */
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
        if(displayedStudentID==null)
            displayedStudentID=me.getId();
        displayedStudent = new Student(displayedStudentID);
        isInEditMode = false;
        final boolean displayedStudentIsMe = displayedStudentID.equals(me.getId());

        editProfileImageView = (ImageView)findViewById(R.id.imageViewEditProfile);

        profilePicImageView = (ImageView)findViewById(R.id.imageViewProfilePic);
        displayedStudent.getPicture(items -> {
            byte[] decodedString = Base64.decode(items, Base64.DEFAULT);
            Bitmap decodedBytes = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profilePicImageView.setImageBitmap(decodedBytes);

        });
        //TODO: RETRIEVE PROFILE PIC

        activeSwitch = (Switch)findViewById(R.id.activeSwitch);
        displayedStudent.getStatus(items -> activeSwitch.setChecked(items));

        nameEditText = (EditText)findViewById(R.id.editTextName);
        displayedStudent.getFullName(items -> nameEditText.setText(items));
        toggleEditable(nameEditText);

        coursesEditText = (EditText)findViewById(R.id.editTextCourses);
        toggleEditable(coursesEditText);
        displayedStudent.getCourses(items -> {
            String coursesListAsString = "";
            if(items.size() > 0) {
                for (int i = 0; i < items.size() - 1; i++) {
                    coursesListAsString += items.get(i).getString("courseName") + ", ";
                }
                coursesListAsString += items.get(items.size() - 1).getString("courseName");
            }
            coursesEditText.setText(coursesListAsString);
        });

        gpaEditText = (EditText)findViewById(R.id.editTextGPA);
        displayedStudent.getGPA(items -> {
            gpaEditText.setText(String.format("%.2f", items));
            if(gpaEditText.getText().toString().equals("0.00"))
                gpaEditText.setText("");
        });

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

        ratingBar = findViewById(R.id.ratingBar);
        displayedStudent.getRating(items -> ratingBar.setRating(items.getDouble("rating").floatValue()));
        ratingBar.setIsIndicator(true);

        if(!displayedStudentIsMe){
            editProfileImageView.setVisibility(View.INVISIBLE);
            emailEditText.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
            (findViewById(R.id.textViewEmailLabel)).setVisibility(View.INVISIBLE);
            activeSwitch.setClickable(false);
        }
        else{
            displayedStudent.getEmail(items -> emailEditText.setText(items));
            deleteClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
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
                            Intent intent =
                                    new Intent(UserProfileActivity.this, Bindr.class);
                            startActivity(intent);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
        }
    }

    /**
     * sets the active/inactive status of the user.
     * @param v - view calling this method (the activeSwitch)
     */
    public void onActiveSwitchClicked(View v){
        if(!me.getId().equals(displayedStudent.getId()))
            return;
        if(activeSwitch.isChecked()){ //user wants to be active
            me.setStatus(true, items -> {});
        }
        else{ //user wants to be inactive
            me.setStatus(false, items -> {});
        }
    }

    /**
     * lets user edit their name, gpa, bio, interests, and email
     * note the button that calls this method is only visible if the displayed user
     *  is the current user
     * @param v
     */
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

    /**
     * if the user is in edit mode, lets the user select a new profile picture
     * otherwise, does nothing
     * @param v - the view calling this method (i.e., the profile pic ImageView)
     */
    public void onProfilePicClicked(View v){
        if(isInEditMode){
            //TODO: IMPLEMENT
            //Should allow user to select a new profile picture
            openGallery();
        }
        //else do nothing
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            profilePicImageView.setImageURI(imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                me.editPicture(encoded);

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed to get bitmap of profile pic");
            }
        } //else do nothing
    }

    /**
     * writes the user's edits to the database and exits edit mode
     * @param v - the view calling this method (the confirm button)
     */
    public void onConfirmClicked(View v){
        exitEditMode();
        //Force withdraw keyboard:
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        displayedStudent.editName(nameEditText.getText().toString(), items -> {});
        displayedStudent.editGPA(Double.parseDouble(gpaEditText.getText().toString()),
                items -> {});
        displayedStudent.editBio(bioEditText.getText().toString(), items -> {});
        displayedStudent.editEmail(emailEditText.getText().toString(), items -> {});
        displayedStudent.editInterests(interestsEditText.getText().toString());
    }

    /**
     * makes the edittexts uneditable, sets isInEditMode to false
     * hides confirm, cancel buttons
     * re-shows edit profile ImageView (the pencil)
     */
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

    /**
     * restores the EditTexts as though the user never tried to edit
     * exits edit mode
     * @param v
     */
    public void onCancelClicked(View v){
        //Force withdraw keyboard:
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        exitEditMode();
        restoreValues();
    }

    /**
     * restores previous values of name, gpa, bio, interests, email
     */
    private void restoreValues(){
        nameEditText.setText((String)nameEditText.getTag(R.id.TAG_TEXT_INDEX));
        gpaEditText.setText((String)gpaEditText.getTag(R.id.TAG_TEXT_INDEX));
        bioEditText.setText((String)bioEditText.getTag(R.id.TAG_TEXT_INDEX));
        interestsEditText.setText((String)interestsEditText.getTag(R.id.TAG_TEXT_INDEX));
        emailEditText.setText((String)emailEditText.getTag(R.id.TAG_TEXT_INDEX));
    }

    /**
     * opens dialog that allows user to delete their account
     * @param v - View calling this method (i.e., the Delete Account button)
     */
    public void onDeleteClicked(View v){
        //Force withdraw keyboard:
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        Log.d(TAG, "Trying to delete account...");
        builder.setMessage("Are you sure you want to delete your account? This cannot be undone")
                .setPositiveButton("Yes", deleteClickListener)
                .setNegativeButton("No", deleteClickListener).show();
    }

    /**
     * if isInEditMode is true, saves the current text of the given EditText et as a tag of et,
     *  and makes et Editable
     * Otherwise (if not isInEditMode), makes the given EditText et uneditable
     * @param et
     */
    private void toggleEditable(EditText et){
        if(isInEditMode){
            et.setTag(R.id.TAG_TEXT_INDEX, et.getText().toString());
            et.setKeyListener((KeyListener)(et.getTag(R.id.TAG_KEYLISTENER_INDEX)));
        }
        else{
            et.setTag(R.id.TAG_KEYLISTENER_INDEX, et.getKeyListener());
            et.setKeyListener(null);
        }
    }

    /* Start Navigation Stuff */
    //Navbar closes on activity change

    /**
     * when the back arrow button is pressed, goes back to previous activity
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
     * takes user to the Activity selected from the nav bar
     * @param item - the item of the nav bar selected
     * @return
     */
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
