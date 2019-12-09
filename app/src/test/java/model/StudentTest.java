package model;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StudentTest {

    private Student student;
    private final String student_id = "1";

    @Before
    public void setup(){
        student = new Student(student_id);
    }

    @Test
    public void getID() {
        String id = student.getId();
        assertTrue(id.equals(student_id));
    }

    //Since writing our initial tests, our implementation of the methods changed greatly as any interaction with the Student model call the database
    //This would no longer be unit tests and instead would be integration testing
}
