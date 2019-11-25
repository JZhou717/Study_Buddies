package model;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class StudentTest {

    private Student student;
    private Student matched;
    private Student passed;
    private final String student_id = "1";
    private final String match_id = "2";
    private final String  pass_id = "3";
    private Course course;
    private final int SCHOOL_ID = 1;
    private final int DEPARTMENT_ID = 198;
    private final int COURSE_ID = 431;
    private final String COURSE_NAME = "Software Engineering";
    private List<Course> courses;

    @Before
    public void setup(){
        student = new Student(student_id);
        matched = new Student(match_id);
        passed = new Student(pass_id);

        course = new Course(SCHOOL_ID, DEPARTMENT_ID, COURSE_ID, COURSE_NAME);
        courses = new ArrayList<Course>();
    }

    @Test
    public void testAddChatRoom() {
        student.addChatRoom("room name");
        assertTrue(student.getChatRooms().size() == 1);
    }

    @Test
    public void testRemoveChatRoom() {
        student.removeChatRoom("room name");
        assertTrue(student.getChatRooms().size() == 0);
    }

    @Test
    public void testAddCourse() {
        student.addCourse(course);
        assertTrue(student.getCourses().size() == 1);
    }

    @Test
    public void testRemoveCourse() {
        student.removeCourse(course);
        assertTrue(student.getCourses().size() == 0);
    }

    @Test
    public void testPopulateStudentInfo() {
        student.populateStudentInfo();
        //populate should retrieve multiple fields but if one is there, all should be there
        assertTrue(student.getCourses().equals(courses));
    }

    @Test
    public void testAddMatchedStudent() {
        student.addMatchedStudent(match_id);
        assertTrue(student.getMatched().size() == 1);
    }

    @Test
    public void testAddPassedStudent() {
        student.addPassedStudent(pass_id);
        assertTrue(student.getPassed().size() == 1);
    }

    @Test
    public void testDeleteAccount() {
        student = student.deleteAccount();
        assertTrue(student == null);
    }

}
