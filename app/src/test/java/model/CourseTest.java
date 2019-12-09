package model;

import com.study.bindr.Bindr;
import com.study.bindr.DatabaseCallBack;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class CourseTest {
    private Course course;
    private final String SCHOOL_ID = "03";
    private final String DEPARTMENT_ID = "300";
    private final String COURSE_ID = "300";
    private final String COURSE_NAME = "Software Engineering";

    @Before
    public void setup() {
        course = new Course(SCHOOL_ID, DEPARTMENT_ID, COURSE_ID, COURSE_NAME);
    }

    @Test
    public void testGetSchoolID() {
        assertEquals(SCHOOL_ID, course.getSchoolID());
    }


    @Test
    public void testGetDepartmentID() {
        assertEquals(DEPARTMENT_ID, course.getDepartmentID());
    }

    @Test
    public void testGetCourseID() {
        assertEquals(COURSE_ID, course.getCourseID());
    }

    @Test
    public void testGetCourseName(){
        assertEquals(COURSE_NAME, course.getCourseName());
    }
}
