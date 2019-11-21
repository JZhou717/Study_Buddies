package model;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class StudentTest {
    private Student student;
    private final int id = 1;

    @Before
    public void setup(){
        student = new Student(id);
    }

}
