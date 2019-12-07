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
        course.getStudentIDsInCourse(new DatabaseCallBack<List<String>>() {
            @Override
            public void onCallback(List<String> items) {
                studentIDsInCourse = items;
                studentIDsInCourseIterator = studentIDsInCourse.listIterator();
            }
        });


        profilePictureImageView = (ImageView)findViewById(R.id.imageViewProfilePic);
        nameTextView = (TextView)findViewById(R.id.textViewName);
        coursesTextView = (TextView)findViewById(R.id.textViewCourses);
        gpaTextView = (TextView)findViewById(R.id.textViewGPA); //TODO check GPA type when getting from student
        bioTextView = (TextView)findViewById(R.id.textViewBio);


        profilePictureImageView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //TODO: MUST ACTUALLY RETRIEVE THE SPECIFIC USER'S PROFILE
                //(currently, we're just loading a mockup of a profile)
                Intent i = new Intent(MatchActivity.this,UserProfileActivity.class);
                startActivity(i);
            }
        });

        displayNextStudent();
    }
    private void displayNextStudent(){
        while(studentIDsInCourseIterator==null) { //students have yet to load
            Snackbar.make(findViewById(R.id.buttonReject),
                    "Loading students...", Snackbar.LENGTH_SHORT).show();
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(!studentIDsInCourseIterator.hasNext()){
            Snackbar.make(findViewById(R.id.buttonReject),
                    "No other students in this course found", Snackbar.LENGTH_INDEFINITE)
                    .show();
            return;
        }
        Student nextStudent = new Student(studentIDsInCourseIterator.next());
        nextStudent.getCourses(new DatabaseCallBack<List<Document>>() {
            @Override
            public void onCallback(List<Document> items) {
                String coursesOfNextStudent = "";
                for(int i=0; i<items.size(); i++){
                    coursesOfNextStudent += ", " + items.get(i).getString("courseName");
                }
                coursesTextView.setText(coursesOfNextStudent);
            }
        });
        nextStudent.getFullName(new DatabaseCallBack<String>() {
            @Override
            public void onCallback(String items) {
                nameTextView.setText(items);
            }
        });
        nextStudent.getGPA(new DatabaseCallBack<Double>() {
            @Override
            public void onCallback(Double items) {
                gpaTextView.setText(String.format("%.2f", items));
            }
        });
        nextStudent.getBio(new DatabaseCallBack<String>() {
            @Override
            public void onCallback(String items) {
                bioTextView.setText(items);
            }
        });
    }
}