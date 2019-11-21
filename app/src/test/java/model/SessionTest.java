package model;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import static org.junit.Assert.*;


public class SessionTest {

    private Session session;
    private final Student[] students = new Student[2];
    private final LocalDateTime dateTime = LocalDateTime.now();


    @Before
    public void setup(){
        session = new Session(students,dateTime);
    }

    @Test
    public void testGetStudents(){
        assertTrue(students.equals(session.getStudents()));
    }

    @Test
    public void testGetDateTime(){
        assertTrue(dateTime.equals(session.getDateTime()));
    }
}

