package model;

import org.junit.Before;
import org.junit.Test;

public class CourseTest {
    Course course;

    @Before
    public void setup() {
        int schoolID = 1;
        int departmentID = 198;
        int courseID = 431;
        String courseName = "Software Engineering";
        course = new Course(schoolID, departmentID, courseID, courseName);
    }



}
