package com.study.bindr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.Document;

import model.Student;

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
