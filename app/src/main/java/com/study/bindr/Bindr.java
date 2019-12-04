package com.study.bindr;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;

import model.Student;

public class Bindr extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connect client to database
        BindrController.setUpDatabaseConnection();

        RemoteFindIterable findResults = BindrController.studentsCollection
                .find();

        findResults.forEach(item -> {
            System.out.println("successfully found student doc: "+ item.toString());
        });


    }

    public void login(View view) {
        //TESTING REMOVE LATER
        Student student = new Student("5ddb3fd5c3de9037b0b2ced6");
        student.getEmail(new DatabaseCallBack<String>() {
            @Override
            public void onCallback(String item) {
                System.out.println(item);
            }

        });

        /*
        student.editEmail("test@rutgers.edu", new DatabaseCallBack<Boolean> () {
            @Override
            public void onCallback(Boolean success) {
                if(success.booleanValue() == false) {
                    AlertDialog alert = new AlertDialog.Builder(Bindr.this).create();
                    alert.setTitle("Email Update Unsuccessful");
                    alert.setMessage("Your email did not update properly. Please try again");
                    alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alert.show();
                }
            }
        });
         */
        //END OF TESTING


        Intent intent = new Intent(Bindr.this, Home_Activity.class);
        startActivity(intent);
    }

    public void register(View view) {
        Intent intent = new Intent(Bindr.this, RegisterActivity.class);
        startActivity(intent);
    }
}
