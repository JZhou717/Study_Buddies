package model;
import java.time.LocalDateTime;

public class Session {
    private Student[] students;
    private LocalDateTime dateTime;

    public Session(Student[] students, LocalDateTime dateTime){
        this.students = students;
        this.dateTime = dateTime;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Student[] getStudents() {
        return students;
    }
}
