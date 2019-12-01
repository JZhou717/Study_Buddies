package com.study.bindr;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;

import org.bson.Document;

public class BindrController {
    static StitchAppClient client =
            Stitch.initializeDefaultAppClient("bindr-yfpaz");

    static RemoteMongoClient mongoClient =
            client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
    //get collections
    public static RemoteMongoCollection<Document> studentsCollection=mongoClient.getDatabase("bindr").getCollection("students");
    public static RemoteMongoCollection<Document> chatsCollection=mongoClient.getDatabase("bindr").getCollection("chats");
    public static RemoteMongoCollection<Document> coursesCollection=mongoClient.getDatabase("bindr").getCollection("courses");


    //called in Bindr (login)
    public static void setUpDatabaseConnection(){

        client.getAuth().loginWithCredential(new AnonymousCredential())
                .addOnCompleteListener(new OnCompleteListener<StitchUser>() {
                    @Override
                    public void onComplete(@NonNull final Task<StitchUser> task) {
                        if (task.isSuccessful()) {
                            System.out.println("stitch logged in anonymously");

                        } else {
                            System.out.println("stitch failed to log in anonymously " + task.getException());
                        }

                    }
                });

    }


}
