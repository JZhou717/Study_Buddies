package com.study.bindr;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteFindOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Course;
import model.Student;

public class DatabaseUtility {
    //Returns full names given a list of students ids, in the same order as the given students list
    public static void getFullNameList(DatabaseCallBack<List<String>> dbCallBack, List<String> studentIDs){
//Query by id
        ArrayList<ObjectId> ids=new ArrayList<>();
        for (int i=0; i<studentIDs.size();i++){
            ids.add(new ObjectId(studentIDs.get(i)));
        }
        Document filterDoc = new Document()
                .append("_id", new Document().append("$in", ids));

        RemoteFindIterable findResults = BindrController.studentsCollection
                .find(filterDoc)
                .projection(new Document().append("_id", 1).append("full_name",1));

        Task<List<Document>> itemsTask = findResults.into(new ArrayList<Document>());
        itemsTask.addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    List<Document> items = task.getResult();
                    Log.d("getFullNameList", String.format("successfully found %d documents", items.size()));
                    ArrayList<Document> studentDocs=new ArrayList<>();
                    for (Document item: items) {
                        Log.d("getFullNameList", String.format("successfully found:  %s", item.toString()));
                        studentDocs.add(item);
                    }
                    ArrayList<String> fullNames=new ArrayList<>();
                    //Return full name in correct order
                    for (int i=0 ;i<studentIDs.size();i++){
                        for (int j=0; j<studentDocs.size(); j++){
                            String id=studentDocs.get(j).get("_id").toString();
                            if(studentIDs.get(i).equals(id)){
                                String fullName=studentDocs.get(j).getString("full_name");
                                fullNames.add(fullName);
                                break;
                            }
                        }
                    }
                    dbCallBack.onCallback(fullNames);

                } else {
                    Log.e("getFullNameList", "failed to find documents with: ", task.getException());
                }
            }
        });
    }
    public static void getOnlyStudentsInCourse(DatabaseCallBack<List<String>> dbCallBack, List<String> studentIDs, Course course){
        Document query = new Document().append("schoolID", course.getSchoolID())
                .append("departmentID", course.getDepartmentID())
                .append("courseID", course.getCourseID());

        Document projection = new Document()
                .append("_id", 0)
                .append("students", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        final Task<Document> getStudentsCourse = BindrController.coursesCollection.findOne(query, options);
        getStudentsCourse.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getStudentsInCourse", String.format("No document matches the provided query"));
                    dbCallBack.onCallback(new ArrayList<>());
                }
                else if (task.isSuccessful()) {
                    Log.d("getStudentsInCourse", String.format("Successfully found document: %s",
                            task.getResult()));
                    Document item = task.getResult();
                    List<ObjectId> students= (List<ObjectId>) item.get("students");
                    List<String> filteredStudentIDs=new ArrayList<>();
                    for (int i=0; i<students.size();i++){
                        String id=students.get(i).toString();
                        for (int j=0 ; j<studentIDs.size(); j++){
                            if(id.equals(studentIDs.get(j))){
                                filteredStudentIDs.add(id);
                                break;
                            }
                        }

                    }
                    dbCallBack.onCallback(filteredStudentIDs);

                } else {
                    Log.e("getStudentsInCourse", "Failed to findOne: ", task.getException());
                }
            }
        });
    }


    /**
     * Runs a query to see if the inputted email is already taken by another user
     * @param email the email that we are checking for in the database
     * @param dbCallBack the method that we pass a boolean to on query completion
     */
    protected static void emailTaken(String email, DatabaseCallBack<Boolean> dbCallBack) {
        //Query by field
        Document query = new Document().append("email", email);
        //Hide the id
        Document projection = new Document()
                .append("_id", 0);
        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findTask = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findTask.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("emailTaken", "Email has not been taken");
                    //Email not taken
                    dbCallBack.onCallback(new Boolean(false));
                }
                else if (task.isSuccessful()) {
                    Log.d("emailTaken", String.format("Successfully found document: %s",
                            task.getResult()));
                    //Email taken
                    dbCallBack.onCallback(new Boolean(true));

                } else {
                    Log.e("emailTaken", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * Runs a query to see if the inputted username is already taken by another user
     * @param username the username that we are checking for in the database
     * @param dbCallBack the method that we pass a boolean to on query completion
     */
    static void usernameTaken(String username, DatabaseCallBack<Boolean> dbCallBack) {
        //Query by field
        Document query = new Document().append("username", username);
        //Hide the id
        Document projection = new Document()
                .append("_id", 0);
        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findTask = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findTask.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("usernameTaken", "Username has not been taken");
                    //Username not taken
                    dbCallBack.onCallback(new Boolean(false));
                }
                else if (task.isSuccessful()) {
                    Log.d("usernameTaken", String.format("Successfully found document: %s",
                            task.getResult()));
                    //Username taken
                    dbCallBack.onCallback(new Boolean(true));

                } else {
                    Log.e("usernameTaken", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * Creates this user as a document in the students collection of the database
     * Sets this user as the current logged in user in BindrController
     * @param email email of the user
     * @param username username of the user
     * @param password password of the user
     * @param picture byte array representation of the user image
     * @param fullName full name of the user
     * @param bio bio of the user
     * @param interests interests of the user
     * @param gpa gpa of the user, 0.0 if none inputted
     * @param dbCallBack the callback method to which we pass our boolean of success or fail
     */
    static void createAccount(String email, String username, String password, byte[] picture, String fullName, String bio, String interests, double gpa, DatabaseCallBack<Boolean> dbCallBack) {

        //The document that we are trying to insert
        Document newItem = new Document()
                .append("email", email)
                .append("rating", 0.0)
                .append("rating_count", 0)
                .append("username", username)
                .append("password", password)
                .append("picture", picture)
                .append("full_name", fullName)
                .append("bio", bio)
                .append("interests", interests)
                .append("gpa", gpa);

        final Task <RemoteInsertOneResult> insertTask = BindrController.studentsCollection.insertOne(newItem);

        insertTask.addOnCompleteListener(new OnCompleteListener <RemoteInsertOneResult> () {
            @Override
            public void onComplete(@NonNull Task <RemoteInsertOneResult> task) {
                if (task.isSuccessful()) {
                    Log.d("accountCreation", String.format("successfully inserted item with %s",
                            task.getResult()));

                    String studentID = task.getResult().getInsertedId().toString();
                    studentID = studentID.substring(19, studentID.length() - 1);

                    //Set the current user to the new account
                    BindrController.setCurrentUser(new Student(studentID));
                    //Sends success flag back
                    dbCallBack.onCallback(true);

                } else {
                    Log.e("accountCreation", "failed to insert document with: ", task.getException());
                    //Sends fail flag back
                    dbCallBack.onCallback(false);
                }
            }
        });

    }
}
