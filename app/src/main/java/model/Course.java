package model;

public class Course {

    private int schoolID;
    private int departmentID;
    private int courseID;
    private String courseName;

    public Course(int schoolID, int departmentID, int courseID, String courseName){
        this.schoolID = schoolID;
        this.departmentID = departmentID;
        this.courseID = courseID;
        this.courseName = courseName;
    }

    public int getSchoolID(){
        return schoolID;
    }

    public int getDepartmentID(){
        return departmentID;
    }

    public int getCourseID(){
        return courseID;
    }

    public String getCourseName(){
        return courseName;
    }
}
