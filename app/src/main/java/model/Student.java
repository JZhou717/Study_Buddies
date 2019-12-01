package model;

import android.util.Log;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteFindOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;
import com.study.bindr.BindrController;
import com.study.bindr.ChatsAdapter;
import com.study.bindr.ChatsListActivity;
import com.study.bindr.DatabaseCallBack;
import com.study.bindr.MatchedStudentAdapter;

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

    //public Student(String foo, String bar){ } // TODO: DELETE THIS CONSTRUCTOR

    public Student(String id){
        this.id = id;
        this.populateStudentInfo();
        //TODO: Retrieve student's courses, email, chatRooms, sessions, matched, passed, status from DB

    }

    public void populateStudentInfo(){
        courses = new ArrayList<Course>();
        chatRooms = new ArrayList<String>();
        sessions = new ArrayList<Session>();
        matched = new ArrayList<String>();
        passed = new ArrayList<String>();
        pendingMatches = new ArrayList<String>();
        //TODO: IMPLEMENT
    }

    public String getEmail(){
        return null;
    }
    public String getId(){return this.id;}

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
//        Document query = new Document().append("_id", new ObjectId(id));
//        Document projection = new Document()
//                .append("_id",0)
//                .append("courses",1);
//        RemoteFindOptions options = new RemoteFindOptions()
//                .projection(projection);
//        final Task <Document> findCourses = BindrController.studentsCollection.findOne(query,options);
//        findCourses.addOnCompleteListener(new OnCompleteListener <Document> () {
//            @Override
//            public void onComplete(@NonNull Task <Document> task){
//                if(task.getResult() == null){
//                    Log.d("getCourses", String.format("No document matches the provided query"));
//                } else if (task.isSuccessful()){
//                    Log.d("getCourses", String.format("Successfully found document: %s",
//                            task.getResult()));
//                    Document item = task.getResult();
//                    List<Course> courses = (List<Course>) item.get("courses");
//                    dbCallBack.onCallback(courses);
//                } else {
//                    Log.e("getCourses", "Failed to findOne: ", task.getException());
//                }
//            }
//        });
    }

    public List<Session> getSessions(){
        return sessions;
    }

    public void addCourse(Course course) {
        courses.add(course);
        return;  //TODO: IMPLEMENT FOR COURSE ACTIVITY
    }

    public void removeCourse(Course course) {
        return;  //TODO: IMPLEMENT FOR COURSE ACTIVITY
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
    }

    public void addPassedStudent(String passedStudentID){
        passed.add(passedStudentID);
        //TODO: IMPLEMENT
    }

    public List<String> getPendingMatches(){
        return pendingMatches;
    }

    public String getUsername(){
        return null;  //TODO: GET FROM DB
    }

    public String getPassword(){
        return null;  //TODO: GET FROM DB
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

    public String getBio(){
        return null; //TODO: GET FROM DB
    }

    public double getGPA(){
        return -1;  //TODO: GET FROM DB
    }

    public void editEmail(String newEmail){
        //TODO: IMPLEMENT
    }

    public void editPassword(String newPassword){
        //TODO: IMPLEMENT
    }

    public void editName(String newName){
        //TODO: IMPLEMENT
    }

    public void editBio(String newBio){
        //TODO: IMPLEMENT
    }

    public void editGPA(double newGPA){
        //TODO: IMPLEMENT
    }

    public void setStatus(boolean isActive){
        //TODO: IMPLEMENT
    }

    public Student deleteAccount(){
        return null;
        //TODO: IMPLEMENT
    }


    //Feiying will implement: getMatched gets ALL matched students.
    //This will get the matched students that the user has not chatted with yet.
    public void MatchedNotChatting(DatabaseCallBack<String> dbCallBack){

    }



}
