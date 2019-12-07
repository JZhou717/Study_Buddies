package com.study.bindr;

import android.os.Bundle;
import android.widget.ImageView;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.bson.Document;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import model.Course;
import model.Student;


public class MatchActivity extends AppCompatActivity {
    private ImageView profilePictureImageView;
    private TextView nameTextView;
    private TextView coursesTextView;
    private TextView gpaTextView;
    private TextView bioTextView;
    private List<String> studentIDsInCourse;
    private String idOfDisplayedStudent;
    private final Student me = BindrController.getCurrentUser();
    private ListIterator<String> studentIDsInCourseIterator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //COURSE_CODE is "schoolID:departmentID:courseID"
        String[] courseCodeSplit = getIntent().getStringExtra("COURSE_CODE").split(":");
        String schoolID = courseCodeSplit[0];
        String departmentID = courseCodeSplit[1];
        String courseID = courseCodeSplit[2];
        Course course = new Course(schoolID, departmentID, courseID, "");



        profilePictureImageView = (ImageView)findViewById(R.id.imageViewProfilePic);
        nameTextView = (TextView)findViewById(R.id.textViewName);
        coursesTextView = (TextView)findViewById(R.id.textViewCourses);
        gpaTextView = (TextView)findViewById(R.id.textViewGPA);
        bioTextView = (TextView)findViewById(R.id.textViewBio);


        profilePictureImageView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //TODO: MUST ACTUALLY RETRIEVE THE SPECIFIC USER'S PROFILE
                //(currently, we're just loading a mockup of a profile)
                Intent i = new Intent(MatchActivity.this,UserProfileActivity.class);
                startActivity(i);
            }
        });
        course.getStudentIDsInCourse(items -> {
            studentIDsInCourse = items;
            studentIDsInCourseIterator = studentIDsInCourse.listIterator();
            displayNextStudent(); //once the students are loaded, display potential match.
        });
    }
    private void displayNextStudent(){
        if(!studentIDsInCourseIterator.hasNext()){
            Snackbar.make(findViewById(R.id.buttonReject),
                    "No other students in this course found", Snackbar.LENGTH_INDEFINITE)
                    .show();
            return;
        }
        Student nextStudent = new Student(studentIDsInCourseIterator.next());
        if(nextStudent.getId().equals(me.getId())){
            displayNextStudent();
            return;
        }
        //display nextStudent iff neither student has passed on the other
        // and current user has not already requested to match with nextStudent
        nextStudent.getPassed(items -> {
            if(items.contains(me.getId())){
                displayNextStudent();
                return;
            }
            me.getPassed(items1 -> {
                if(items1.contains(nextStudent.getId())){
                    displayNextStudent();
                    return;
                }
                me.getRequestedMatched(items2 -> {
                    if(items2.contains(nextStudent.getId())){
                        displayNextStudent();
                        return;
                    }
                });
                displayStudent(nextStudent);

            });
        });
    }
    private void displayStudent(Student student){
        student.getCourses(items -> {
            String coursesOfNextStudent = "";
            for(int i=0; i<items.size(); i++){
                coursesOfNextStudent += ", " + items.get(i).getString("courseName");
            }
            coursesTextView.setText(coursesOfNextStudent);
        });
        //TODO: Get profile picture of student
        student.getFullName(items -> nameTextView.setText(items));
        student.getGPA(items -> gpaTextView.setText(String.format("%.2f", items)));
        student.getBio(items -> bioTextView.setText(items));
        idOfDisplayedStudent = student.getId();
    }
    public void match(View v){
        //TODO: IMPLEMENT
    }

    public void pass(View v){
        //TODO: IMPLEMENT
    }
}