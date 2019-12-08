package com.study.bindr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.concurrent.TimeUnit;

import model.Student;

public class Bindr extends AppCompatActivity {

    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Linking view components
        email = (EditText) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.input_password);

        //connect client to database
        BindrController.setUpDatabaseConnection();
    }

    /**
     * On every start of this activity, the user is inherently logged out of the system
     */
    @Override
    protected void onStart() {
        super.onStart();

        //Logout
        BindrController.setCurrentUser(null);
    }

    /**
     * Attempts to login in with the information in the fields provided
     * @param view
     */
    public void login(View view) {

        //Grab the inputted values from the fields
        String email_username = email.getText().toString();
        String pass = password.getText().toString();

        //If it is an email
        if(email_username.indexOf('@') > -1 ) {
            Student.emailLogin(email_username, pass, new DatabaseCallBack<Boolean>() {
                @Override
                public void onCallback(Boolean success) {
                    //Current user already set in the emailLogin method of Student
                    if(success) {
                        //Transition to home page
                        Intent intent = new Intent(Bindr.this, Home_Activity.class);
                        startActivity(intent);
                    }
                    else {
                        failedLogin();
                    }
                }
            });
        } //Else it is a username
        else {
            Student.usernameLogin(email_username, pass, new DatabaseCallBack<Boolean>() {
                @Override
                public void onCallback(Boolean success) {

                    //Current user already set in the emailLogin method of Student
                    if(success) {
                        //Transition to home page
                        Intent intent = new Intent(Bindr.this, Home_Activity.class);
                        startActivity(intent);
                    }
                    else {
                        failedLogin();
                    }
                }
            });
        }
    }

    /**
     * Creates an alert notifying login failed
     */
    private void failedLogin() {
        AlertDialog alert = new AlertDialog.Builder(Bindr.this).create();
        alert.setTitle("Email Login Unsuccessful");
        alert.setMessage("Your email and password did not match an existing account. Please try again");
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert.show();
    }

    /**
     * Moves to the register activity where the user may register
     * @param view
     */
    public void register(View view) {
        Intent intent = new Intent(Bindr.this, RegisterActivity.class);
        startActivity(intent);
    }
}
