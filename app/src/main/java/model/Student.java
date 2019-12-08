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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
                }
                else if (task.isSuccessful()) {
                    Log.d("emailLogin", String.format("Successfully found document: %s",
                            task.getResult()));
                    ObjectId id = task.getResult().getObjectId("_id");
                    String id_string = id.toString();

                    BindrController.setCurrentUser(new Student(id_string));

                    //Sends success flag back
                    dbCallBack.onCallback(new Boolean(true));


                } else {
                    Log.e("emailLogin", "Failed to findOne: ", task.getException());

                    //Sends success flag back
                    dbCallBack.onCallback(new Boolean(false));
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
                    Double bio = task.getResult().getDouble("gpa");

                    //Sends full_name back
                    dbCallBack.onCallback(bio);

                } else {
                    Log.e("getBio", "Failed to findOne: ", task.getException());
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
                        courses=new ArrayList<>();
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

        RemoteUpdateOptions options = new RemoteUpdateOptions().upsert(true);

        final Task<RemoteUpdateResult> removeCourse = BindrController.studentsCollection.updateOne(filterDoc, updateDoc, options);

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
                    List<String> requestedMatchStrings=new ArrayList<>();

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
                    Log.e("addPassed", "failed to update document with: ", task.getException());
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

    /**
     * Runs a query to get a list of Strings of student ids of students that has tried to match with this user
     * @param dbCallBack The method to which the list of Strings is passed to
     */
    public void getPendingMatches(DatabaseCallBack<List<String>> dbCallBack){
        //Query by id
        Document query = new Document().append("_id", new ObjectId(this.id));

        //Project the matches array
        Document projection = new Document()
                .append("_id", 0)
                .append("pending", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task <Document> findPendingMatches = BindrController.studentsCollection.findOne(query, options);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        findPendingMatches.addOnCompleteListener(new OnCompleteListener <Document> () {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getPending", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getPending", String.format("Successfully found document: %s",
                            task.getResult()));
                    //Get the student ID results as a list
                    Document item = task.getResult();
                    List<ObjectId> pending = (List<ObjectId>) item.get("pending");
                    List<String> pendingString =new ArrayList<>();

                    for (int i=0; i< pending.size(); i++){
                        String pendingStudentID = pending.get(i).toString();
                        pendingString.add(pendingStudentID);
                    }
                    dbCallBack.onCallback(pendingString);


                } else {
                    Log.e("getPending", "Failed to findOne: ", task.getException());
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
                    List<String> matchesString=new ArrayList<>();

                    List<Document> chats=(List<Document>) item.get("chats");
                    if (chats==null){
                        chats=new ArrayList<>();
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
                        sessions=new ArrayList<>();
                    }
                    dbCallBack.onCallback(sessions);

                } else {
                    Log.e("getSessions", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

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
                        chatRooms=new ArrayList<>();
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

    public void removeRequestedMatchedStudent(String idOfStudentToBeRemoved){
        //TODO: IMPLEMENT
    }

    public void getRating(DatabaseCallBack<Double> dbCallBack){
        //TODO: IMPLEMENT
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
        //TODO: IMPLEMENT
    }

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

    public void getInterests(DatabaseCallBack <String > databaseCallBack){

    }

}
