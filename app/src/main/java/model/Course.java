package model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.client.model.Filters;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteFindOptions;
import com.study.bindr.BindrController;
import com.study.bindr.DatabaseCallBack;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;

public class Course {

    private int schoolID;
    private int departmentID;
    private int courseID;
    private String courseName;

    public Course(int schoolID, int departmentID, int courseID, String courseName){
        this.schoolID = schoolID;
        this.departmentID = departmentID;
        this.courseID = courseID;
        this.courseName = courseName;
    }

    public int getSchoolID(){ return schoolID; }

    public int getDepartmentID(){
        return departmentID;
    }

    public int getCourseID(){
        return courseID;
    }

    public String getCourseName(){
        return courseName;
    }

    public void getStudentIDsInCourse(DatabaseCallBack<List<String>> dbCallBack){
        //Filter based on school/dept/course ids
        Bson filter = Filters.and(
                Filters.eq("schoolID", schoolID),
                Filters.eq("departmentID", departmentID),
                Filters.eq("courseID", departmentID));

        //Project the students array
        Document projection = new Document()
                .append("_id", 0)
                .append("students", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        //task to find ids of students in this course
        final Task <Document> findStudentIDs = BindrController.studentsCollection
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
}
