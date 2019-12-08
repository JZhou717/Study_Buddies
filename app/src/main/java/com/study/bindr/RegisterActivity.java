package com.study.bindr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import model.Student;

public class RegisterActivity extends AppCompatActivity {

    //Grab the fields that we are using
    EditText emailField;
    EditText usernameField;
    EditText passwordField;
    EditText confirmPasswordField;
    EditText fullNameField;
    EditText bioField;
    EditText interestsField;
    EditText GPAField;

    //The iamge

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set the EditText fields to link to the ones on the xml
        emailField = (EditText) findViewById(R.id.editEmailAddress);
        usernameField = (EditText) findViewById(R.id.editUsername);
        passwordField = (EditText) findViewById(R.id.editPassword);
        confirmPasswordField = (EditText) findViewById(R.id.editConfirmPassword);
        fullNameField = (EditText) findViewById(R.id.editFullName);
        bioField = (EditText) findViewById(R.id.editBiography);
        interestsField = (EditText) findViewById(R.id.editInterests);
        GPAField = (EditText) findViewById(R.id.editGPA);

    }

    /**
     * Grabs the user input from the fields and attempts to create a new account
     */
    public void createAccount(View view) {
        //Force withdraw keyboard:
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        //Grab user inputs
        String email = emailField.getText().toString();
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();
        String fullName = fullNameField.getText().toString();
        String bio = bioField.getText().toString();
        String interests = interestsField.getText().toString();
        String gpa = GPAField.getText().toString();

        

    }

    /**
     * Initiates the upload of an image from the user device to the system
     */
    public void uploadImage(View view) {
        //TODO: IMPLEMENT THIS
    }

    //Transitions to the edit course activity, called when the account creation is successful
    //Sets the current user to the one that was created in createAccount
    private void goToEditCourses(String id) {
        BindrController.setCurrentUser(new Student(id));
        Intent intent = new Intent(RegisterActivity.this, EditCoursesActivity.class);
        startActivity(intent);
    }
}
