package model;

import android.app.AlertDialog;
import android.util.Log;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteDeleteResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteFindOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;
import com.study.bindr.BindrController;
import com.study.bindr.ChatsAdapter;
import com.study.bindr.ChatsListActivity;
import com.study.bindr.DatabaseCallBack;
import com.study.bindr.MatchedStudentAdapter;
import com.study.bindr.UserProfileActivity;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Student implements Serializable {

    private List<Course> courses;
    private List<String> chatRooms;
    private List<Session> sessions;
    private List<String> matched;
    private List<String> passed;
    private List<String> pendingMatches;
    private String id;

    public Student(String id){
        this.id = id;
        //Fills in student class's instance variables from database
        this.populateStudentInfo();

    }

    public void populateStudentInfo(){
        courses = new ArrayList<Course>();
        chatRooms = new ArrayList<String>();
        sessions = new ArrayList<Session>();
        matched = new ArrayList<String>();
        passed = new ArrayList<String>();
        pendingMatches = new ArrayList<String>();
        //TODO: IMPLEMENT
        //We should change the above statements to call the relevant query methods instead
    }

    public void getEmail(DatabaseCallBack<String> dbCallBack){
        //Query by _id
        Document query = new Document().append("_id", new ObjectId(this.id));
        //Project the email
        Document projection = new Document()
                .append("_id", 0)
                .append("email", 1);
        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findEmail = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findEmail.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getEmail", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getEmail", String.format("Successfully found document: %s",
                            task.getResult()));
                            String email = task.getResult().getString("email");

                            //Sends full_name back
                            dbCallBack.onCallback(email);


                } else {
                    Log.e("getEmail", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * Takes in new email to upload to database, notifies success to call back method
     * @param newEmail String of new email
     * @param dbCallBack gets passed true if successful update, false otherwise
     */
    public void editEmail(String newEmail, DatabaseCallBack<Boolean> dbCallBack){
        //Query to find the document to edit
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document of changes that we have to make
        Document updateDoc = new Document().append("$set",
                new Document()
                .append("email", newEmail)
        );

        final Task<RemoteUpdateResult> updateTask = BindrController.studentsCollection.updateOne(filterDoc, updateDoc);

        //listens for the update query and logs response
        updateTask.addOnCompleteListener(new OnCompleteListener <RemoteUpdateResult> () {
            @Override
            public void onComplete(@NonNull Task <RemoteUpdateResult> task) {

                boolean update_successful;

                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("app", String.format("successfully matched %d and modified %d documents for update email",
                            numMatched, numModified));

                    update_successful = true;

                } else {
                    Log.e("app", "failed to update email of document with: ", task.getException());

                    update_successful = false;
                }

                //Sends result back back
                dbCallBack.onCallback(update_successful);
            }
        });
    }

    public void getFullName(DatabaseCallBack<String> dbCallBack){
        //Query by _id
        Document query = new Document().append("_id", new ObjectId(this.id));
        //Project the full_name
        Document projection = new Document()
                .append("_id", 0)
                .append("full_name", 1);
        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findFullName = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findFullName.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getFullName", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getFullName", String.format("Successfully found document: %s",
                            task.getResult()));
                    Document item = task.getResult();
                    String full_name=item.getString("full_name");

                    //Sends full_name back
                    dbCallBack.onCallback(full_name);

                } else {
                    Log.e("getFullName", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    public void editName(String newName, DatabaseCallBack<Boolean> dbCallBack){
        //Query to find the document to edit
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document of changes that we have to make
        Document updateDoc = new Document().append("$set",
                new Document()
                        .append("full_name", newName)
        );

        final Task<RemoteUpdateResult> updateTask = BindrController.studentsCollection.updateOne(filterDoc, updateDoc);

        //listens for the update query and logs response
        updateTask.addOnCompleteListener(new OnCompleteListener <RemoteUpdateResult> () {
            @Override
            public void onComplete(@NonNull Task <RemoteUpdateResult> task) {

                boolean update_successful;

                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("app", String.format("successfully matched %d and modified %d documents for update full_name",
                            numMatched, numModified));

                    update_successful = true;

                } else {
                    Log.e("app", "failed to update full_name of document with: ", task.getException());

                    update_successful = false;
                }

                //Sends result back back
                dbCallBack.onCallback(update_successful);
            }
        });
    }

    public void getUsername(DatabaseCallBack<String> dbCallBack){
        //Query by _id
        Document query = new Document().append("_id", new ObjectId(this.id));
        //Project the username
        Document projection = new Document()
                .append("_id", 0)
                .append("username", 1);
        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findUsername = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findUsername.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getUsername", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getUsername", String.format("Successfully found document: %s",
                            task.getResult()));
                    String username = task.getResult().getString("username");

                    //Sends full_name back
                    dbCallBack.onCallback(username);


                } else {
                    Log.e("getUsername", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    public void getPassword(DatabaseCallBack<String> dbCallBack){
        //Query by _id
        Document query = new Document().append("_id", new ObjectId(this.id));
        //Project the username
        Document projection = new Document()
                .append("_id", 0)
                .append("password", 1);
        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findPassword = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findPassword.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getPassword", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getPassword", String.format("Successfully found document: %s",
                            task.getResult()));
                    String password = task.getResult().getString("password");

                    //Sends full_name back
                    dbCallBack.onCallback(password);

                } else {
                    Log.e("getPassword", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    public void editPassword(String newPassword, DatabaseCallBack<Boolean> dbCallBack){
        //Query to find the document to edit
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document of changes that we have to make
        Document updateDoc = new Document().append("$set",
                new Document()
                        .append("password", newPassword)
        );

        final Task<RemoteUpdateResult> updateTask = BindrController.studentsCollection.updateOne(filterDoc, updateDoc);

        //listens for the update query and logs response
        updateTask.addOnCompleteListener(new OnCompleteListener <RemoteUpdateResult> () {
            @Override
            public void onComplete(@NonNull Task <RemoteUpdateResult> task) {

                boolean update_successful;

                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("app", String.format("successfully matched %d and modified %d documents for update password",
                            numMatched, numModified));

                    update_successful = true;

                } else {
                    Log.e("app", "failed to update password of document with: ", task.getException());

                    update_successful = false;
                }

                //Sends result back back
                dbCallBack.onCallback(update_successful);
            }
        });
    }

    public void getBio(DatabaseCallBack<String> dbCallBack){
        //Query by _id
        Document query = new Document().append("_id", new ObjectId(this.id));
        //Project the username
        Document projection = new Document()
                .append("_id", 0)
                .append("bio", 1);
        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findBio = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findBio.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getBio", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getBio", String.format("Successfully found document: %s",
                            task.getResult()));
                    String bio = task.getResult().getString("bio");

                    //Sends full_name back
                    dbCallBack.onCallback(bio);

                } else {
                    Log.e("getBio", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    public void editBio(String newBio, DatabaseCallBack<Boolean> dbCallBack){
        //Query to find the document to edit
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document of changes that we have to make
        Document updateDoc = new Document().append("$set",
                new Document()
                        .append("bio", newBio)
        );

        final Task<RemoteUpdateResult> updateTask = BindrController.studentsCollection.updateOne(filterDoc, updateDoc);

        //listens for the update query and logs response
        updateTask.addOnCompleteListener(new OnCompleteListener <RemoteUpdateResult> () {
            @Override
            public void onComplete(@NonNull Task <RemoteUpdateResult> task) {

                boolean update_successful;

                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("app", String.format("successfully matched %d and modified %d documents for update bio",
                            numMatched, numModified));

                    update_successful = true;

                } else {
                    Log.e("app", "failed to update bio of document with: ", task.getException());

                    update_successful = false;
                }

                //Sends result back back
                dbCallBack.onCallback(update_successful);
            }
        });
    }

    public void getGPA(DatabaseCallBack<Double> dbCallBack){
        //Query by _id
        Document query = new Document().append("_id", new ObjectId(this.id));
        //Project the username
        Document projection = new Document()
                .append("_id", 0)
                .append("gpa", 1);
        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findGpa = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findGpa.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getGpa", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getGpa", String.format("Successfully found document: %s",
                            task.getResult()));
                    Double bio = task.getResult().getDouble("gpa");

                    //Sends full_name back
                    dbCallBack.onCallback(bio);

                } else {
                    Log.e("getBio", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    public void editGPA(double newGPA, DatabaseCallBack<Boolean> dbCallBack){
        //Query to find the document to edit
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document of changes that we have to make
        Document updateDoc = new Document().append("$set",
                new Document()
                        .append("gpa", newGPA)
        );

        final Task<RemoteUpdateResult> updateTask = BindrController.studentsCollection.updateOne(filterDoc, updateDoc);

        //listens for the update query and logs response
        updateTask.addOnCompleteListener(new OnCompleteListener <RemoteUpdateResult> () {
            @Override
            public void onComplete(@NonNull Task <RemoteUpdateResult> task) {

                boolean update_successful;

                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("app", String.format("successfully matched %d and modified %d documents for update gpa",
                            numMatched, numModified));

                    update_successful = true;

                } else {
                    Log.e("app", "failed to update gpa of document with: ", task.getException());

                    update_successful = false;
                }

                //Sends result back back
                dbCallBack.onCallback(update_successful);
            }
        });
    }

    public void setStatus(boolean isActive, DatabaseCallBack<Boolean> dbCallBack){
        //Query to find the document to edit
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document of changes that we have to make
        Document updateDoc = new Document().append("$set",
                new Document()
                        .append("status", isActive)
        );

        final Task<RemoteUpdateResult> updateTask = BindrController.studentsCollection.updateOne(filterDoc, updateDoc);

        //listens for the update query and logs response
        updateTask.addOnCompleteListener(new OnCompleteListener <RemoteUpdateResult> () {
            @Override
            public void onComplete(@NonNull Task <RemoteUpdateResult> task) {

                boolean update_successful;

                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("app", String.format("successfully matched %d and modified %d documents for update status",
                            numMatched, numModified));

                    update_successful = true;

                } else {
                    Log.e("app", "failed to update status of document with: ", task.getException());

                    update_successful = false;
                }

                //Sends result back back
                dbCallBack.onCallback(update_successful);
            }
        });
    }

    public String getId(){
        return this.id;
    }

    public void getChatRooms(DatabaseCallBack<List<Document>> dbCallBack){
        //Query by id
        Document query = new Document().append("_id", new ObjectId(id));

        //Project the chats array
        Document projection = new Document()
                .append("_id", 0)
                .append("chats", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task <Document> findChatRooms = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findChatRooms.addOnCompleteListener(new OnCompleteListener <Document> () {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getChatRooms", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getChatRooms", String.format("Successfully found document: %s",
                            task.getResult()));
                    Document items = task.getResult();
                    //Get the chatrooms results as a list
                    List<Document> chatRooms= (List<Document>) items.get("chats");
                    dbCallBack.onCallback(chatRooms);

                } else {
                    Log.e("getChatRooms", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    public void addChatRoom(String room){
        chatRooms.add(room);
        //TODO: IMPLEMENT
    }

    //Saves new chat room into database
    public void saveChatRoom(String room, String studentID){
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        Document updateDoc = new Document().append("$push",
                new Document().append("chats", new Document().append("room", room)
                        .append("student", new ObjectId(studentID)))
        );

        RemoteUpdateOptions options = new RemoteUpdateOptions().upsert(true);

        final Task<RemoteUpdateResult> saveChatRoom =
                BindrController.studentsCollection.updateOne(filterDoc, updateDoc, options);
        saveChatRoom.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("saveChatRoom", String.format("successfully matched %d and modified %d documents",
                            numMatched, numModified));
                } else {
                    Log.e("saveChatRoom", "failed to update document with: ", task.getException());
                }
            }
        });

    }
    public void removeChatRoom(String room) {
        //TODO: IMPLEMENT
    }

    public List<Course> getCourses(){
        return courses;
//
    }

    public List<Session> getSessions(){
        return sessions;
    }

    public void addCourse(Course course) {
        courses.add(course);
        return;  //TODO: IMPLEMENT FOR COURSE ACTIVITY, ADD TO STUDENT'S DOCUMENT'S COURSES FIELD
    }

    public void removeCourse(Course course) {
        return;  //TODO: IMPLEMENT FOR COURSE ACTIVITY REMOVE FROM STUDENT'S DOCUMENT'S COURSES FIELD
    }

    public void getMatched(DatabaseCallBack<List<String>> dbCallBack){
        //Query by id
        Document query = new Document().append("_id", new ObjectId(this.id));

        //Project the matches array
        Document projection = new Document()
                .append("_id", 0)
                .append("matches", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task <Document> findMatchedStudents = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findMatchedStudents.addOnCompleteListener(new OnCompleteListener <Document> () {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getMatched", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getMatched", String.format("Successfully found document: %s",
                            task.getResult()));
                    //Get the student ID results as a list
                    Document item = task.getResult();
                    List<ObjectId> matches= (List<ObjectId>) item.get("matches");
                    List<String> matchesString=new ArrayList<>();

                    for (int i=0; i<matches.size(); i++){
                        String matchedStudentID=matches.get(i).toString();
                        matchesString.add(matchedStudentID);
                    }
                    dbCallBack.onCallback(matchesString);


                } else {
                    Log.e("getMatched", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    public void addMatchedStudent(String matchedStudentID){
        matched.add(matchedStudentID);
        //TODO: IMPLEMENT
    }

    public List<String> getPassed(){
        return passed;
        //TODO: IMPLEMENT
    }

    public void addPassedStudent(String passedStudentID){
        passed.add(passedStudentID);
        //TODO: IMPLEMENT
    }

    public List<String> getPendingMatches(){
        return pendingMatches;
        //TODO: IMPLEMENT
    }

    public void deleteAccount(){
        //Query to find the student document related to this user
        Document query = new Document()
                .append("_id", new ObjectId(this.id));

        final Task<RemoteDeleteResult> deleteTask = BindrController.studentsCollection.deleteOne(query);
        deleteTask.addOnCompleteListener(new OnCompleteListener <RemoteDeleteResult> () {
            @Override
            public void onComplete(@NonNull Task <RemoteDeleteResult> task) {
                if (task.isSuccessful()) {
                    long numDeleted = task.getResult().getDeletedCount();
                    Log.d("app", String.format("successfully deleted %d documents", numDeleted));
                } else {
                    Log.e("app", "REQUIRE MANUAL STUDENT DELETION. failed to delete document with: ", task.getException());
                }
            }
        });

        //Remove the current user
        //CALLER SHOULD LOGOUT WHEN CALLING THIS
        BindrController.setCurrentUser(null);
    }


    //Feiying will implement: getMatched gets ALL matched students.
    //This will get the matched students that the user has not chatted with yet.
    public void MatchedNotChatting(DatabaseCallBack<String> dbCallBack){

    }



}
