package com.study.bindr;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.content.Intent;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.ListIterator;

import model.Course;
import model.Student;


public class MatchActivity extends AppCompatActivity {
    private Button matchButton = (Button)findViewById(R.id.buttonMatch);
    private Button passButton = (Button)findViewById(R.id.buttonPass);
    private ImageView profilePictureImageView;
    private TextView nameTextView;
    private TextView coursesTextView;
    private TextView gpaTextView;
    private TextView bioTextView;
    private RatingBar ratingBar;
    private List<String> studentIDsInCourse;
    private String idOfDisplayedStudent;
    private final Student me = BindrController.getCurrentUser();
    private ListIterator<String> studentIDsInCourseIterator;

    /**
     * sets up initial layout and starts loading other students.
     * @param savedInstanceState - data for this instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        matchButton.setVisibility(View.INVISIBLE);
        passButton.setVisibility(View.INVISIBLE);
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
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        profilePictureImageView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent i = new Intent(MatchActivity.this,UserProfileActivity.class);
                i.putExtra("STUDENT_ID", idOfDisplayedStudent);
                startActivity(i);
            }
        });
        course.getStudentIDsInCourse(items -> {
            studentIDsInCourse = items;
            studentIDsInCourseIterator = studentIDsInCourse.listIterator();
            displayNextStudent(); //once the students are loaded, display potential match.
        });
    }

    /**
     * displays the next potential match for the course if one exists
     * otherwise, tells the current user no other users in the course were found
     * Note: a potential match is another user who has NOT passed on the current user
     *    and who has NOT been passed on by the current user
     *    and who has NOT been requested to match with by the current user
     */
    private void displayNextStudent(){
        //hide the match/pass buttons while we are loading the next student
        matchButton.setVisibility(View.INVISIBLE);
        passButton.setVisibility(View.INVISIBLE);
        if(!studentIDsInCourseIterator.hasNext()){
            Snackbar.make(findViewById(R.id.buttonPass),
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
                    //if we get to this point, all the aforementioned conditions are met
                    // so display nextStudent
                    displayStudent(nextStudent);
                });
            });
        });
    }

    /**
     * Displays the given student
     * @param student - the student to display
     */
    private void displayStudent(Student student){
        student.getCourses(items -> {
            String coursesOfNextStudent = "";
            //There is an obscure edge case that might occur if the other student
            // removes their courses while we're loading the student for display,
            // which would mean they don't have any courses to display.
            //In practice, this won't happen for our app, but this if statement is just in case.
            if(items.size() > 0) {
                for (int i = 0; i < items.size() - 1; i++) {
                    coursesOfNextStudent += ", " + items.get(i).getString("courseName");
                }
                coursesOfNextStudent += items.get(items.size() - 1).getString("courseName");
            }
            coursesTextView.setText(coursesOfNextStudent);
        });
        //TODO: Get profile picture of student
        student.getFullName(items -> nameTextView.setText(items));
        student.getGPA(items -> gpaTextView.setText(String.format("%.2f", items)));
        student.getBio(items -> bioTextView.setText(items));
        idOfDisplayedStudent = student.getId();
        //unhide the match and pass buttons once the other student is displayed
        matchButton.setVisibility(View.VISIBLE);
        passButton.setVisibility(View.VISIBLE);
    }

    /**
     * updates database based on the current user requesting to match with the displayed student
     * @param v - the view calling this method (i.e., match button)
     */
    public void match(View v){
        Student otherStudent = new Student(idOfDisplayedStudent);
        final String FULL_NAME_OF_OTHER_STUDENT = nameTextView.getText().toString();
        //IF me.id is in otherStudent's requestedMatches,
        //  remove me.id from otherStudent's requestedMatches
        //  add me.id to otherStudents matches
        //  add otherStudent.id to me's matches
        //  display "Matched! You can now message <otherStudent's full name>!"
        //OTHERWISE (if me.id is NOT in otherStudent's requestedMatches),
        //  add otherStudent.id to me's requestedMatches
        //FINALLY, display the next student
        otherStudent.getRequestedMatched(items -> {
            if(items.contains(me.getId())){
                otherStudent.removeRequestedMatchedStudent(me.getId());
                otherStudent.addMatchedStudent(me.getId());
                me.addMatchedStudent(otherStudent.getId());
                Snackbar.make(v, String.format("Matched! You can now message %s!",
                        FULL_NAME_OF_OTHER_STUDENT),
                        Snackbar.LENGTH_SHORT).show();
            }
            else{
                me.addRequestedMatchedStudent(otherStudent.getId());
            }
        });
        displayNextStudent();
    }

    /**
     * updates database based on the current user passing on the displayed student
     * @param v - the view calling this method (i.e., the pass button)
     */
    public void pass(View v){
        //Add otherStudent.id to me's passed
        //Display the next student
        me.addPassedStudent(idOfDisplayedStudent);
        displayNextStudent();
    }
}