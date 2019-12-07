package model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.client.model.Filters;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteFindOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;
import com.study.bindr.BindrController;
import com.study.bindr.DatabaseCallBack;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.List;

public class Course {

    private String schoolID;
    private String departmentID;
    private String courseID;
    private String courseName;

    /**
     * Constructor
     * @param schoolID
     * @param departmentID
     * @param courseID
     * @param courseName
     *
     */
    public Course(String schoolID, String departmentID, String courseID, String courseName){
        this.schoolID = schoolID;
        this.departmentID = departmentID;
        this.courseID = courseID;
        this.courseName = courseName;
    }

    /**
     * get the schoolID
     * @return the school ID
     */
    public String getSchoolID(){ return schoolID; }

    /**
     * get the department ID
     * @return the department ID
     */
    public String getDepartmentID(){
        return departmentID;
    }

    /**
     * get the course ID
     * @return the course ID
     */
    public String getCourseID(){
        return courseID;
    }

    /**
     * get the course name
     * @return the course name
     */
    public String getCourseName(){
        return courseName;
    }

    /**
     * get a BSON filter to find this course in the courses collection
     * @return a BSON filter to find this course in the courses collection
     */
    private Bson docEqualsFilter() {
        return Filters.and(
                Filters.eq("schoolID", schoolID),
                Filters.eq("departmentID", departmentID),
                Filters.eq("courseID", courseID));
    }

    /**
     * calls dbCallBack(studentIDs)
     * where studentIDs is a list of the ids (as strings) of the students in this course
     * @param dbCallBack - an implementation of the interface DatabaseCallBack
     */
    public void getStudentIDsInCourse(DatabaseCallBack<List<String>> dbCallBack){
        //Filter based on school/dept/course ids
        Bson filter = docEqualsFilter();
        //Project the students array
        Document projection = new Document()
                .append("_id", 0)
                .append("students", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        //task to find ids of students in this course
        final Task <Document> findStudentIDs = BindrController.coursesCollection
                .findOne(filter, options);

        //listens for when the query finishes and sends result to callback method
        // (given in parameter)
        findStudentIDs.addOnCompleteListener(new OnCompleteListener <Document> () {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getStudentIDsInCourse",
                            String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getStudentIDsInCourse",
                            String.format("Successfully found document: %s", task.getResult()));
                    Document item = task.getResult();
                    //Get the studentIDs results as a list
                    List<String> studentIDs= (List<String>)item.get("students");
                    dbCallBack.onCallback(studentIDs);

                } else {
                    Log.e("getStudentIDsInCourse",
                            "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * adds the student with the given student ID to the coruse in the database
     * @param studentID
     */
    public void addStudentToThisCourseInDatabase(String studentID){
        Bson filter = docEqualsFilter();
        Log.d("addStudentToThisCourse",
                String.format("Looking for %s:%s:%s", schoolID, departmentID, courseID));
        //Project just the id
        Document projection = new Document()
                .append("_id", 0);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        //task to find object id of course matching this
        final Task <Document> findCourse = BindrController.coursesCollection
                .findOne(filter, options);

        //listens for when the query finishes and updates flag courseExistsInCollection
        findCourse.addOnCompleteListener(new OnCompleteListener <Document> () {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                final Document newCourse = new Document()
                        .append("schoolID", schoolID)
                        .append("departmentID", departmentID)
                        .append("courseID", courseID)
                        .append("students", Arrays.asList(new ObjectId(studentID)));
                if (task.getResult() == null) {
                    Log.d("findCourse",
                            String.format("No document matches the provided query"));
                    final Task <RemoteInsertOneResult> insertNewCourse
                            = BindrController.coursesCollection.insertOne(newCourse);
                    insertNewCourse.addOnCompleteListener(new OnCompleteListener <RemoteInsertOneResult> ()
                    {
                        @Override
                        public void onComplete(@NonNull Task <RemoteInsertOneResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("insertNewCourse",
                                        String.format("successfully inserted item with id %s",
                                        task.getResult().getInsertedId()));
                                Log.d("insertNewCourse", String.format("Course SAVED"));

                            } else {
                                Log.e("insertNewCourse",
                                        "failed to insert document with: ",
                                        task.getException());
                            }
                        }
                    });
                }
                else if (task.isSuccessful()) {
                    Log.d("findCourse",
                            String.format("Successfully found document: %s", task.getResult()));
                    Document courseDocumentIDAsDocument = task.getResult();
                    Document studentIDDocument = new Document()
                            .append("students", new ObjectId(studentID));
                    Document updateCourseStudentsArrayDocument = new Document()
                            .append("$addToSet", studentIDDocument);

                    //add the student to the course array
                    Task<RemoteUpdateResult> updateCourseStudentsArray =
                            BindrController.coursesCollection.updateOne(
                                    courseDocumentIDAsDocument, updateCourseStudentsArrayDocument);
                    updateCourseStudentsArray.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
                        @Override
                        public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("updateCourse","successfully updated course");

                            } else {
                                Log.e("updateCourse","failed to update course with: ",
                                        task.getException());
                            }
                        }
                    });

                } else {
                    Log.e("findCourse",
                            "Failed to findOne: ", task.getException());
                    final Task <RemoteInsertOneResult> insertNewCourse
                            = BindrController.coursesCollection.insertOne(newCourse);
                    insertNewCourse.addOnCompleteListener(new OnCompleteListener <RemoteInsertOneResult> ()
                    {
                        @Override
                        public void onComplete(@NonNull Task <RemoteInsertOneResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("insertNewCourse",
                                        String.format("successfully inserted item with id %s",
                                                task.getResult().getInsertedId()));
                                Log.d("insertNewCourse", String.format("Course SAVED"));

                            } else {
                                Log.e("insertNewCourse",
                                        "failed to insert document with: ",
                                        task.getException());
                            }
                        }
                    });
                }
            }
        });
        while(!findCourse.isComplete());
    }

    /**
     * Removes the student matching the given studentID from the course in the database
     * @param studentID
     */
    public void removeStudentFromThisCourseInDatabase(String studentID){
        Log.d("Course.removeStudent...", String.format("Attempting to remove %s", studentID));
        Bson filter = docEqualsFilter();
        Document studentIDDoc = new Document()
                .append("students", new ObjectId(studentID));
        Document removeCourseStudentsArrayDocument = new Document()
                .append("$pull", studentIDDoc);
        Task<RemoteUpdateResult> task = BindrController.coursesCollection
                .updateOne(filter, removeCourseStudentsArrayDocument);
        task.addOnCompleteListener(new OnCompleteListener <RemoteUpdateResult> ()
        {
            @Override
            public void onComplete(@NonNull Task <RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    Log.d("removeStudent",
                            String.format("successfully removed with id %s",
                                    studentID));
                } else {
                    Log.e("removeStudent",
                            "failed to remove student with: ",
                            task.getException());
                }
            }
        });
    }

    /**
     * method to determine if this course matches the given course
     * @param course
     * @return true if the courses' schoolID, departmentID, and courseID match, false otherwise.
     */
    public boolean equals(Course course){
        if(course==null)
            return false;
        return schoolID.equals(course.getSchoolID())
                && departmentID.equals(course.getDepartmentID())
                && courseID.equals(course.getCourseID());
    }
}
