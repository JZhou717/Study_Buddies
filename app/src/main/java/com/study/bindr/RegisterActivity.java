package com.study.bindr;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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

    //Flag for image retrieval
    private static final int RESULT_LOAD_IMAGE = 1;
    //Max size of image that user can upload in bytes
    private static final int MAX_IMAGE_SIZE = 1048576;

    //Byte representation of user image
    byte[] picture = new byte[0];
    //Flag to see if we have valid login info
    private boolean valid_info = true;

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
        String gpaString = GPAField.getText().toString();
        Double gpa;

        valid_info = true;

        /* * * Validate fields * * */
        //Check email
        if(email.indexOf('@') == -1 || email.indexOf('.') == -1) {
            createAlert("Invalid email input", "Please enter a valid email");
            return;
        }
        //Check if email is taken
        DatabaseUtility.emailTaken(email, new DatabaseCallBack<Boolean>() {
            @Override
            public void onCallback(Boolean taken) {
                if(taken) {
                    createAlert("Email Taken", "Please enter a new unique email");
                }
            }
        });

        //Check username
        if(username.equals("")) {
            createAlert("Invalid username input", "Please enter a valid username");
            return;
        }
        //Check if username is taken
        DatabaseUtility.usernameTaken(username, new DatabaseCallBack<Boolean>() {
            @Override
            public void onCallback(Boolean taken) {
                if(taken) {
                    createAlert("Username Taken", "Please enter a new unique username");
                }
            }
        });

        //Check password
        if(password.equals("")) {
            createAlert("Invalid password input", "Please enter a valid password");
            return;
        }
        if(!password.equals(confirmPassword)) {
            createAlert("Invalid password input", "Please ensure your password matches your confirmation password");
            return;
        }

        //Check gpa
        if(gpaString.equals("")) {
            gpaString = "0";
        }
        try {
            gpa = Double.parseDouble(gpaString);
        } catch (NumberFormatException e) {
            createAlert("Invalid gpa input", "Please input a valid gpa of the format x.y where x and y are whole numbers");
            return;
        }
        /* * * End of validating fields * * */

        //Wait 2 second for validation queries to complete
        try{
            TimeUnit.SECONDS.sleep(2);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        if(valid_info) {
            
            //Create Account
            DatabaseUtility.createAccount(email, username, password, picture, fullName, bio, interests, gpa, new DatabaseCallBack<Boolean>() {
                @Override
                public void onCallback(Boolean success) {
                    if (success) {
                        goToEditCourses();
                    } else {
                        createAlert("Account creation failed", "Something went wrong in trying to create your account. Please try again");
                    }
                }
            });
        }
    }

    /**
     * Initiates the upload of an image from the user device to the system
     */
    public void uploadImage(View view) {
        //Starting activity to get image
        Intent getImage = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(getImage, RESULT_LOAD_IMAGE);
    }

    /**
     * Extends the existing onActivityResult to grab the user uploaded image as a bitmap
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                //The image file as a bitmap that we grabbed
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                //Converting bitmap to byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                picture = stream.toByteArray();
                bitmap.recycle();

                //Check to make sure image is not too large
                if(picture.length > MAX_IMAGE_SIZE) {
                    createAlert("Image too large", "Please select a new image of size 1MB or smaller");
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                createAlert("Image Upload Failure", "Could not find the image you attempted to upload");
            } catch (IOException e) {
                e.printStackTrace();
                createAlert("Image Upload Failure", "Could not find the image you attempted to upload");
            }
        }
    }

    /**
     * Helper method that creates an alert dialog with the given title and message
     * Alert has the OK button
     * @param title Title of the alert
     * @param message Message in the alert
     */
    private void createAlert(String title, String message) {
        valid_info = false;

        AlertDialog alert = new AlertDialog.Builder(RegisterActivity.this).create();
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert.show();
    }

    //Transitions to the edit course activity, called when the account creation is successful
    private void goToEditCourses() {
        Intent intent = new Intent(RegisterActivity.this, EditCoursesActivity.class);
        startActivity(intent);
    }
}
