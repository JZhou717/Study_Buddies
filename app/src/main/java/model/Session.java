package model;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class Session implements Serializable {
    private String partnerID;
    private Date dateTime;

    private int reminder;

    public Session(String partnerID, Date dateTime, int reminder) {
        this.partnerID = partnerID;
        this.dateTime = dateTime;
        this.reminder=reminder;
    }
    public Date getDateTime() {
        return dateTime;
    }

    public int getReminder() {
        return reminder;
    }

    public String getPartnerID() {
        return partnerID;
    }
/*private Student[] students;
    private LocalDateTime dateTime;

    public Session(Student[] students, LocalDateTime dateTime){
        this.students = students;
        this.dateTime = dateTime;
    }

    public Student[] getStudents() {
        return students;
    }*/
}
