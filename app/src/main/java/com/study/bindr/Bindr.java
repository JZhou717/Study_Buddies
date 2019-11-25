package com.study.bindr;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;

public class Bindr extends AppCompatActivity {
    //Need this for our drawer layout
    //private DrawerLayout drawer;

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
        Intent intent = new Intent(Bindr.this, Home_Activity.class);
        startActivity(intent);
    }

    public void register(View view) {
        Intent intent = new Intent(Bindr.this, RegisterActivity.class);
        startActivity(intent);
    }
}
