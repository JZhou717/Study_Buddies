package model;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteDeleteResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteFindOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;
import com.study.bindr.BindrController;
import com.study.bindr.DatabaseCallBack;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Student implements Serializable {

    private String id;

    /**
     * Constructor that takes in the id of the student
     * All student data is saved in the database
     * @param id Student id equal to the object id of the Student in the database
     */
    public Student(String id){
        this.id = id;
    }

    /**
     * Takes the input email and password and checks to see if a student document with this info exists
     * If so, set the current user in BindrController to the student id with the found document
     * If not, pass back false for the success of the operation to Bindr (login)
     * @param email email associated with student account
     * @param password password associated with student account
     * @param dbCallBack call back method that is passed true if successful or false otherwise
     */
    public static void emailLogin(String email, String password, DatabaseCallBack<Boolean> dbCallBack) {
        //Query to find the document to edit
        Document query = new Document()
                .append("email", email)
                .append("password", password);
        //Project the id
        Document projection = new Document()
                .append("_id", 1);
        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findUser = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findUser.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("emailLogin", String.format("No document matches the provided query"));
                    dbCallBack.onCallback(false);

                }
                else if (task.isSuccessful()) {
                    Log.d("emailLogin", String.format("Successfully found document: %s",
                            task.getResult()));
                    ObjectId id = task.getResult().getObjectId("_id");
                    String id_string = id.toString();

                    BindrController.setCurrentUser(new Student(id_string));

                    //Sends success flag back
                    dbCallBack.onCallback(true);


                } else {
                    Log.e("emailLogin", "Failed to findOne: ", task.getException());

                    //Sends success flag back
                    dbCallBack.onCallback(false);
                }
            }
        });
    }

    /**
     * Takes the input username and password and checks to see if a student document with this info exists
     * If so, set the current user in BindrController to the student id with the found document
     * If not, pass back false for the success of the operation to Bindr (login)
     * @param username username associated with student account
     * @param password password associated with student account
     * @param dbCallBack call back method that is passed true if successful or false otherwise
     */
    public static void usernameLogin(String username, String password, DatabaseCallBack<Boolean> dbCallBack) {
        //Query to find the document to edit
        Document query = new Document()
                .append("username", username)
                .append("password", password);
        //Project the id
        Document projection = new Document()
                .append("_id", 1);
        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findUser = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findUser.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("usernameLogin", String.format("No document matches the provided query"));
                    dbCallBack.onCallback(false);
                }
                else if (task.isSuccessful()) {
                    Log.d("usernameLogin", String.format("Successfully found document: %s",
                            task.getResult()));
                    ObjectId id = task.getResult().getObjectId("_id");
                    String id_string = id.toString();

                    BindrController.setCurrentUser(new Student(id_string));

                    //Sends success flag back
                    dbCallBack.onCallback(new Boolean(true));


                } else {
                    Log.e("usernameLogin", "Failed to findOne: ", task.getException());

                    //Sends success flag back
                    dbCallBack.onCallback(new Boolean(false));
                }
            }
        });
    }

    /**
     * Gets the id of this student object
     * @return String of the student id, also the ObjectID in the database for this student's student document
     */
    public String getId(){
        return this.id;
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

    /**
     * Runs a query to find the email associated with this Student object's id
     * Email is passed as a parameter to the callback method
     * @param dbCallBack method to which the email is passed to
     */
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
     * Takes in new image as binary array to upload to database
     * @param newImage String encoding (Base64) of byte array representation of the user image
     */
    public void editPicture(String newImage){
        //Query to find the document to edit
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document of changes that we have to make
        Document updateDoc = new Document().append("$set",
                new Document()
                        .append("picture", newImage)
        );

        final Task<RemoteUpdateResult> updateTask = BindrController.studentsCollection.updateOne(filterDoc, updateDoc);

        //listens for the update query and logs response
        updateTask.addOnCompleteListener(new OnCompleteListener <RemoteUpdateResult> () {
            @Override
            public void onComplete(@NonNull Task <RemoteUpdateResult> task) {

                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("app", String.format("successfully matched %d and modified %d documents for update picture",
                            numMatched, numModified));
                } else {
                    Log.e("app", "failed to update picture of document with: ", task.getException());
                }
            }
        });
    }

    /**
     * Runs a query to find the profile image associated with this Student object's id
     * Image as a encoded byte array (String in Base64) is passed as a parameter to the callback method
     * @param dbCallBack method to which the image is passed to
     */
    public void getPicture(DatabaseCallBack<String> dbCallBack){
        //Query by _id
        Document query = new Document().append("_id", new ObjectId(this.id));
        //Project the picture
        Document projection = new Document()
                .append("_id", 0)
                .append("picture", 1);
        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findTask = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findTask.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getPicture", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getPicture", String.format("Successfully found document: %s",
                            task.getResult()));

                    String encoded_picture = task.getResult().getString("picture");

                    //Sends full_name back
                    dbCallBack.onCallback(encoded_picture);

                } else {
                    Log.e("getPicture", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * Takes in new name to upload to database, notifies success to call back method
     * @param newName String of new student full name
     * @param dbCallBack gets passed true if successful update, false otherwise
     */
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

    /**
     * Runs a query to find the full name associated with this Student object's id
     * Full name is passed as a parameter to the callback method
     * @param dbCallBack method to which the full name is passed to
     */
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

    /**
     * Runs a query to find the full username associated with this Student object's id
     * Username is passed as a parameter to the callback method
     * @param dbCallBack method to which the username is passed to
     */
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

    /**
     * Takes in new password to upload to database, notifies success to call back method
     * @param newPassword String of new student password
     * @param dbCallBack gets passed true if successful update, false otherwise
     */
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

    /**
     * Runs a query to find the password associated with this Student object's id
     * Password is passed as a parameter to the callback method
     * @param dbCallBack method to which the password is passed to
     */
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

    /**
     * Takes in new bio to upload to database, notifies success to call back method
     * @param newBio String of new student bio
     * @param dbCallBack gets passed true if successful update, false otherwise
     */
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

    /**
     * Runs a query to find the bio associated with this Student object's id
     * Bio is passed as a parameter to the callback method
     * @param dbCallBack method to which the Bio is passed to
     */
    public void getBio(DatabaseCallBack<String> dbCallBack){
        //Query by _id
        Document query = new Document().append("_id", new ObjectId(this.id));
        //Project the Bio
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

    /**
     * Takes in new gpa to upload to database, notifies success to call back method
     * @param newGPA double of new student gpa
     * @param dbCallBack gets passed true if successful update, false otherwise
     */
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

    /**
     * Runs a query to find the GPA associated with this Student object's id
     * GPA is passed as a parameter to the callback method
     * @param dbCallBack method to which the GPA is passed to
     */
    public void getGPA(DatabaseCallBack<Double> dbCallBack){
        //Query by _id
        Document query = new Document().append("_id", new ObjectId(this.id));
        //Project the GPA
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
                    Double gpa = task.getResult().getDouble("gpa");

                    //Sends full_name back
                    dbCallBack.onCallback(gpa);

                } else {
                    Log.e("getGpa", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * Takes in a string of the user inputted for their interests section to upload to database
     * @param newInterest double of new student gpa
     */
    public void editInterests(String newInterest){
        //Query to find the document to edit
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document of changes that we have to make
        Document updateDoc = new Document().append("$set",
                new Document()
                        .append("interests", newInterest)
        );

        final Task<RemoteUpdateResult> updateTask = BindrController.studentsCollection.updateOne(filterDoc, updateDoc);

        //listens for the update query and logs response
        updateTask.addOnCompleteListener(new OnCompleteListener <RemoteUpdateResult> () {
            @Override
            public void onComplete(@NonNull Task <RemoteUpdateResult> task) {

                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("app", String.format("successfully matched %d and modified %d documents for update gpa",
                            numMatched, numModified));
                } else {
                    Log.e("app", "failed to update gpa of document with: ", task.getException());
                }
            }
        });
    }

    /**
     * Runs a query to find the string representing the interests associated with this Student object's id
     * String interests is passed as a parameter to the callback method
     * @param dbCallBack method to which the GPA is passed to
     */
    public void getInterests(DatabaseCallBack<String> dbCallBack){

        //Query by _id
        Document query = new Document().append("_id", new ObjectId(this.id));
        //Project the GPA
        Document projection = new Document()
                .append("_id", 0)
                .append("interests", 1);
        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findTask = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findTask.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getInterests", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getInterests", String.format("Successfully found document: %s",
                            task.getResult()));
                    String interests = task.getResult().getString("interests");

                    //Sends full_name back
                    dbCallBack.onCallback(interests);

                } else {
                    Log.e("getInterests", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * Takes in the current account status to upload to database, notifies success to call back method
     * @param isActive boolean representing account status. True if active, false if inactive
     * @param dbCallBack gets passed true if successful update, false otherwise
     */
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

    /**
     * Runs a query to find the status (active or inactive) associated with this Student object's id
     * isActive is passed as a parameter to the callback method
     * @param dbCallBack method to which the status is passed to
     */
    public void getStatus(DatabaseCallBack<Boolean> dbCallBack) {
        //Query by _id
        Document query = new Document().append("_id", new ObjectId(this.id));
        //Project the status
        Document projection = new Document()
                .append("_id", 0)
                .append("status", 1);
        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findStatus = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findStatus.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getStatus", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getStatus", String.format("Successfully found document: %s",
                            task.getResult()));
                    Boolean status = task.getResult().getBoolean("status");

                    //Sends full_name back
                    dbCallBack.onCallback(status);

                } else {
                    Log.e("getStatus", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * Removes the document with this Student's id from the database. Sets the current user to null
     */
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

    /**
     * Runs a query to retrieve documents representing the list of courses that the user with this studentID would be in
     * @param dbCallBack The method to which the list is passed to
     */
    public void getCourses(DatabaseCallBack<List<Document>> dbCallBack){
        //Query by id
        Document query = new Document().append("_id", new ObjectId(id));

        //Project the chats array
        Document projection = new Document()
                .append("_id", 0)
                .append("courses", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task <Document> findCourses = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findCourses.addOnCompleteListener(new OnCompleteListener <Document> () {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getCourses", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getCourses", String.format("Successfully found document: %s",
                            task.getResult()));
                    Document items = task.getResult();
                    //Get the chatrooms results as a list
                    List<Document> courses = (List<Document>) items.get("courses");
                    if (courses==null){
                        courses=new ArrayList<Document>();
                    }
                    dbCallBack.onCallback(courses);

                } else {
                    Log.e("getCourses", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * Inserts the provided course to the user's Student database document's list of courses
     * @param course the course to be inserted
     */
    public void addCourse(Course course) {

        //Get the fields of the course we are uploading
        String schoolID = course.getSchoolID();
        String departmentID = course.getDepartmentID();
        String courseID = course.getCourseID();
        String courseName = course.getCourseName();

        //Query for the document relating to this student object by their shared ID
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document listing the updates that we are performing
        Document updateDoc = new Document().append("$push",
                new Document().append("courses",
                        new Document()
                                .append("schoolID", schoolID)
                                .append("departmentID", departmentID)
                                .append("courseID", courseID)
                                .append("courseName", courseName))
        );

        RemoteUpdateOptions options = new RemoteUpdateOptions().upsert(true);

        final Task<RemoteUpdateResult> addCourse = BindrController.studentsCollection.updateOne(filterDoc, updateDoc, options);

        addCourse.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("addCourse", String.format("successfully matched %d and modified %d documents",
                            numMatched, numModified));
                } else {
                    Log.e("addCourse", "failed to update document with: ", task.getException());
                }
            }
        });
    }

    /**
     * Removes the given course from the list of courses for this particular student
     * @param course the course they are no longer in
     */
    public void removeCourse(Course course) {

        //Get the fields of the course we are removing
        String schoolID = course.getSchoolID();
        String departmentID = course.getDepartmentID();
        String courseID = course.getCourseID();
        String courseName = course.getCourseName();

        //Query for the document relating to this student object by their shared ID
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document listing the updates that we are performing
        Document updateDoc = new Document().append("$pull",
                new Document().append("courses",
                        new Document()
                                .append("schoolID", schoolID)
                                .append("departmentID", departmentID)
                                .append("courseID", courseID)
                                .append("courseName", courseName))
        );

        final Task<RemoteUpdateResult> removeCourse = BindrController.studentsCollection.updateOne(filterDoc, updateDoc);

        removeCourse.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("removeCourse", String.format("successfully matched %d and modified %d documents",
                            numMatched, numModified));
                } else {
                    Log.e("removeCourse", "failed to update document with: ", task.getException());
                }
            }
        });
    }

    /**
     * Runs a query to get a list of Strings of student ids of students this user has matched with
     * @param dbCallBack The method to which the list of Strings is passed to
     */
    public void getRequestedMatched(DatabaseCallBack<List<String>> dbCallBack){
        //Query by id
        Document query = new Document().append("_id", new ObjectId(this.id));

        //Project the matches array
        Document projection = new Document()
                .append("_id", 0)
                .append("requestedMatches", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task <Document> findMatchedStudents = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findMatchedStudents.addOnCompleteListener(new OnCompleteListener <Document> () {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getRequestedMatched", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getRequestedMatched", String.format("Successfully found document: %s",
                            task.getResult()));
                    //Get the student ID results as a list
                    Document item = task.getResult();

                    List<ObjectId> requestedMatches= (List<ObjectId>) item.get("requestedMatches");
                    if(requestedMatches == null) {
                        requestedMatches = new ArrayList<ObjectId>();
                    }
                    List<String> requestedMatchStrings = new ArrayList<>();

                    for (int i=0; i<requestedMatches.size(); i++){
                        String matchedStudentID=requestedMatches.get(i).toString();
                        requestedMatchStrings.add(matchedStudentID);
                    }
                    dbCallBack.onCallback(requestedMatchStrings);


                } else {
                    Log.e("getRequestedMatched", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * Adds the parameter to this Student's list of matches
     * @param requestedMatchedStudent the string id equivalent to the objectid of the matched student in the student collection of the database
     */
    public void addRequestedMatchedStudent(String requestedMatchedStudent){

        //Query for the document relating to this student object by their shared ID
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document listing the updates that we are performing
        Document updateDoc = new Document().append("$push",
                new Document().append("requestedMatches", new ObjectId(requestedMatchedStudent))
        );

        RemoteUpdateOptions options = new RemoteUpdateOptions().upsert(true);

        final Task<RemoteUpdateResult> addMatched = BindrController.studentsCollection.updateOne(filterDoc, updateDoc, options);

        addMatched.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("addRequestedMatched", String.format("successfully matched %d and modified %d documents",
                            numMatched, numModified));
                } else {
                    Log.e("addRequestedMatched", "failed to update document with: ", task.getException());
                }
            }
        });
    }

    /**
     * Removes the student indicated by the parameter from this student's list of potential matches
     * @param idOfStudentToBeRemoved
     */
    public void removeRequestedMatchedStudent(String idOfStudentToBeRemoved){

        //Query for the document relating to this student object by their shared ID
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document listing the updates that we are performing
        Document updateDoc = new Document().append("$pull",
                new Document().append("requestedMatches", new ObjectId(idOfStudentToBeRemoved))
        );

        final Task<RemoteUpdateResult> addMatched = BindrController.studentsCollection.updateOne(filterDoc, updateDoc);

        addMatched.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("removeRequestedMatched", String.format("successfully matched %d and modified %d documents",
                            numMatched, numModified));
                } else {
                    Log.e("removeRequestedMatched", "failed to update document with: ", task.getException());
                }
            }
        });
    }

    /**
     * Runs a query to get a list of Strings of student ids of students this user has matched with
     * @param dbCallBack The method to which the list of Strings is passed to
     */
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
                    if(matches == null) {
                        matches = new ArrayList<ObjectId>();
                    }

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

    /**
     * Adds the parameter to this Student's list of matches
     * @param matchedStudentID the string id equivalent to the objectid of the matched student in the student collection of the database
     */
    public void addMatchedStudent(String matchedStudentID){

        //Query for the document relating to this student object by their shared ID
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document listing the updates that we are performing
        Document updateDoc = new Document().append("$push",
                new Document().append("matches", new ObjectId(matchedStudentID))
        );

        RemoteUpdateOptions options = new RemoteUpdateOptions().upsert(true);

        final Task<RemoteUpdateResult> addMatched = BindrController.studentsCollection.updateOne(filterDoc, updateDoc, options);

        addMatched.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("addMatched", String.format("successfully matched %d and modified %d documents",
                            numMatched, numModified));
                } else {
                    Log.e("addMatched", "failed to update document with: ", task.getException());
                }
            }
        });
    }

    /**
     * Runs a query to get a list of Strings of student ids of students this user has passed on or blocked
     * @param dbCallBack The method to which the list of Strings is passed to
     */
    public void getPassed(DatabaseCallBack<List<String>> dbCallBack) {
        //Query by id
        Document query = new Document().append("_id", new ObjectId(this.id));

        //Project the matches array
        Document projection = new Document()
                .append("_id", 0)
                .append("passed", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findPassedStudents = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findPassedStudents.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task<Document> task) {
                if (task.getResult() == null) {
                    Log.d("getPassed", String.format("No document matches the provided query"));
                } else if (task.isSuccessful()) {
                    Log.d("getPassed", String.format("Successfully found document: %s",
                            task.getResult()));
                    //Get the student ID results as a list
                    Document item = task.getResult();
                    List<ObjectId> passed = (List<ObjectId>) item.get("passed");
                    if(passed == null) {
                        passed = new ArrayList<ObjectId>();
                    }
                    List<String> passedString = new ArrayList<>();

                    for (int i = 0; i < passed.size(); i++) {
                        String passedStudentID = passed.get(i).toString();
                        passedString.add(passedStudentID);
                    }
                    dbCallBack.onCallback(passedString);

                } else {
                    Log.e("getPassed", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * Adds the parameter to this Student's list of passed
     * @param passedStudentID the string id equivalent to the objectid of the passed  student in the student collection of the database
     */
    public void addPassedStudent(String passedStudentID){

        //Query for the document relating to this student object by their shared ID
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document listing the updates that we are performing
        Document updateDoc = new Document().append("$push",
                new Document().append("passed", new ObjectId(passedStudentID))
        );

        RemoteUpdateOptions options = new RemoteUpdateOptions().upsert(true);

        final Task<RemoteUpdateResult> addPassed = BindrController.studentsCollection.updateOne(filterDoc, updateDoc, options);

        addPassed.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("addPassed", String.format("successfully matched %d and modified %d documents",
                            numMatched, numModified));
                } else {
                    Log.e("addPassed", "failed to update document with: ", task.getException());
                }
            }
        });
    }

    //This will get the matched students that the user has not chatted with yet.
    public void getMatchedNotChatting(DatabaseCallBack<List<String>> dbCallBack){
//Query by id
        Document query = new Document().append("_id", new ObjectId(id));

        //Project the chats array
        Document projection = new Document()
                .append("_id", 0)
                .append("chats", 1)
                .append("matches", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task <Document> findMatchesNotChatting = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findMatchesNotChatting.addOnCompleteListener(new OnCompleteListener <Document> () {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getMatchedNotChatting", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getMatchedNotChatting", String.format("Successfully found document: %s",
                            task.getResult()));
                    //Get the student ID results as a list
                    Document item = task.getResult();
                    List<ObjectId> matches= (List<ObjectId>) item.get("matches");
                    if(matches == null) {
                        matches = new ArrayList<ObjectId>();
                    }

                    List<String> matchesString=new ArrayList<>();

                    List<Document> chats=(List<Document>) item.get("chats");
                    if (chats==null){
                        chats=new ArrayList<Document>();
                    }
                    for (int i = 0; i < matches.size(); i++) {
                        String matchedStudentID = matches.get(i).toString();
                        matchesString.add(matchedStudentID);
                        for (int j = 0; j < chats.size(); j++) {
                            String chatStudentID = chats.get(j).get("student").toString();
                            if (chatStudentID.equals(matchedStudentID)) {
                                System.out.println("Student is chatting" + chatStudentID);
                                matchesString.remove(matchedStudentID);
                            }
                        }
                    }
                    dbCallBack.onCallback(matchesString);



                } else {
                    Log.e("getMatchedNotChatting", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * Runs a query to find the study sessions that this user has created
     * @param dbCallBack the method to which the list of sessions is returned
     */
    public void getSessions(DatabaseCallBack<List<Document>> dbCallBack){
        //Query by id
        Document query = new Document().append("_id", new ObjectId(id));

        //Project the chats array
        Document projection = new Document()
                .append("_id", 0)
                .append("sessions", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task <Document> findSessions = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findSessions.addOnCompleteListener(new OnCompleteListener <Document> () {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getSessions", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getSessions", String.format("Successfully found document: %s",
                            task.getResult()));
                    Document items = task.getResult();
                    //Get the chatrooms results as a list
                    List<Document> sessions = (List<Document>) items.get("sessions");
                    if (sessions==null){
                        sessions=new ArrayList<Document>();
                    }
                    dbCallBack.onCallback(sessions);

                } else {
                    Log.e("getSessions", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * Adds a session to the student's list of sessions
     * @param date of session
     * @param reminder time in minutes before session to get a reminder
     * @param studentID the id of the other student in the session
     */
    public void addSession(Date date, int reminder, String studentID){
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        Document updateDoc = new Document().append("$push",
                new Document().append("sessions", new Document().append("partner", new ObjectId(studentID))
                        .append("datetime", date)
                        .append("reminder", reminder))
        );

        RemoteUpdateOptions options = new RemoteUpdateOptions().upsert(true);

        final Task<RemoteUpdateResult> addSessionTask =
                BindrController.studentsCollection.updateOne(filterDoc, updateDoc, options);
        addSessionTask.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("addSession", String.format("successfully matched %d and modified %d documents",
                            numMatched, numModified));
                } else {
                    Log.e("addSession", "failed to update document with: ", task.getException());
                }
            }
        });
    }

    /**
     * Runs a query for this student's list of chatrooms that they are in
     * @param dbCallBack the method to which the list of chatrooms (as documents) are passed to
     */
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
                    if (chatRooms==null){
                        chatRooms=new ArrayList<Document>();
                    }
                    dbCallBack.onCallback(chatRooms);

                } else {
                    Log.e("getChatRooms", "Failed to findOne: ", task.getException());
                }
            }
        });
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

    /**
     * Removes a chatroom from this user's list of chat rooms
     * @param room
     */
    public void removeChatRoom(String room) {
        //Query for the document relating to this student object by their shared ID
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document listing the updates that we are performing
        Document updateDoc = new Document().append("$pull",
                new Document().append("chats",
                        new Document()
                                .append("room", room)
                ));

        RemoteUpdateOptions options = new RemoteUpdateOptions().upsert(true);

        final Task<RemoteUpdateResult> removeChats = BindrController.studentsCollection.updateOne(filterDoc, updateDoc, options);

        removeChats.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("removeChatRoom", String.format("successfully matched %d and modified %d documents",
                            numMatched, numModified));
                } else {
                    Log.e("removeChatRoom", "failed to update document with: ", task.getException());
                }
            }
        });
    }

    /**
     * Returns a Document object with the fields of "rating" and "rating_count" from the database
     * The rating is the average of all ratings from previous sessions
     * The number of ratings is tracked by rating count
     * @param dbCallBack The method to which the rating and rating count document is passed to
     */
    public void getRating(DatabaseCallBack<Document> dbCallBack){
        //Query by id
        Document query = new Document().append("_id", new ObjectId(id));

        //Project the rating double and rating_count integer
        Document projection = new Document()
                .append("rating", 1)
                .append("rating_count", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task <Document> findTask = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findTask.addOnCompleteListener(new OnCompleteListener <Document> () {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getRating", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getRating", String.format("Successfully found document: %s",
                            task.getResult()));

                    //Returns the result of the query as a document
                    dbCallBack.onCallback(task.getResult());

                } else {
                    Log.e("getRating", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * First computes the average A of the given ratings;
     * that is,
     *  A := (focusRating + productivityRating + engagementRating + environmentRating)/4.0
     * Then updates the student's rating in the database to be
     *  (A + student.rating*student.numRates)/(1 + student.numRates)
     * @param focusRating - new focus rating to be added
     * @param productivityRating - new productivity rating to be added
     * @param engagementRating - new engagement rating to be added
     * @param environmentRating - new environment rating to be added
     */
    public void addNewRating(double focusRating, double productivityRating, double engagementRating,
                             double environmentRating){

        //Run the query to find the current rating and rating_count
        this.getRating(new DatabaseCallBack<Document>() {
            @Override
            public void onCallback(Document ratings) {
                //Save existing data to fields
                double current_rating = ratings.getDouble("rating");
                int current_rating_count = ratings.getInteger("rating_count");
                ObjectId our_id = ratings.getObjectId("_id");

                double input_rating = (focusRating + productivityRating + engagementRating + environmentRating) / 4.0;
                double new_rating = (input_rating + (current_rating_count * current_rating)) / (current_rating_count + 1);

                //Query to find the document to edit
                Document filterDoc = new Document().append("_id", our_id);
                //Document of changes that we have to make
                Document updateDoc = new Document().append("$set",
                        new Document()
                                .append("rating", new_rating)
                                .append("rating_count", (current_rating_count+1))
                );

                final Task<RemoteUpdateResult> updateTask = BindrController.studentsCollection.updateOne(filterDoc, updateDoc);

                //listens for the update query and logs response
                updateTask.addOnCompleteListener(new OnCompleteListener <RemoteUpdateResult> () {
                    @Override
                    public void onComplete(@NonNull Task <RemoteUpdateResult> task) {

                        if (task.isSuccessful()) {
                            long numMatched = task.getResult().getMatchedCount();
                            long numModified = task.getResult().getModifiedCount();
                            Log.d("app", String.format("successfully matched %d and modified %d documents for update rating",
                                    numMatched, numModified));

                        } else {
                            Log.e("app", "failed to update gpa of document with: ", task.getException());

                        }
                    }
                });

            }
        });
    }

    /**
     * Runs a query to get chat rooms from students
     */
    public void getChatRoomsFromStudents(DatabaseCallBack<List<Document>> dbCallBack, List<String> studentIDs){
        Document query = new Document().append("_id",new ObjectId(id));

        Document projection = new Document()
                .append("_id", 0)
                .append("chats", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        final Task<Document> getChatRoomsFromStudents = BindrController.studentsCollection.findOne(query, options);
        getChatRoomsFromStudents.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("chatRoomsFromStudents", String.format("No document matches the provided query"));
                    dbCallBack.onCallback(new ArrayList<Document>());
                }
                else if (task.isSuccessful()) {
                    Log.d("chatRoomsFromStudents", String.format("Successfully found document: %s",
                            task.getResult()));
                    Document item = task.getResult();
                    List<Document> chatRooms= (List<Document>) item.get("chats");
                    if(chatRooms == null) {
                        chatRooms = new ArrayList<Document>();
                    }

                    List<Document> filteredChatrooms=new ArrayList<>();
                    for (int i=0; i<chatRooms.size();i++){
                        Document chat=chatRooms.get(i);
                        String id=chat.get("student").toString();
                        for (int j=0 ; j<studentIDs.size(); j++){
                            if(id.equals(studentIDs.get(j))){
                                filteredChatrooms.add(chat);
                                break;
                            }
                        }

                    }
                    dbCallBack.onCallback(filteredChatrooms);

                } else {
                    Log.e("chatRoomsFromStudents", "Failed to findOne: ", task.getException());
                }
            }
        });
    }
    public void removeMatchedStudent(String studentIDToRemove) {

        //Query for the document relating to this student object by their shared ID
        Document filterDoc = new Document().append("_id", new ObjectId(this.id));
        //Document listing the updates that we are performing
        Document updateDoc = new Document().append("$pull",
                new Document().append("matches",
                        new Document()
                                .append("$in", Arrays.asList(new ObjectId(studentIDToRemove))))
        );

        final Task<RemoteUpdateResult> removeMatchedStudent = BindrController.studentsCollection.updateOne(filterDoc, updateDoc);

        removeMatchedStudent.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("removeMatchedStudent", String.format("successfully matched %d and modified %d documents",
                            numMatched, numModified));
                } else {
                    Log.e("removeMatchedStudent", "failed to update document with: ", task.getException());
                }
            }
        });
    }

}
