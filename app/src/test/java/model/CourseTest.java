package model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CourseTest {
    private Course course;
    private final int SCHOOL_ID = 1;
    private final int DEPARTMENT_ID = 198;
    private final int COURSE_ID = 431;
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
