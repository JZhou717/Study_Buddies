package model;

public class Course {

    int schoolID;
    int departmentID;
    int courseID;
    String courseName;

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
        return courseID;
    }

    public int getCourseID(){
        return courseID;
    }

    public String getCourseName(){
        return courseName;
    }
}
