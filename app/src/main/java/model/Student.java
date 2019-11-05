package model;

import java.util.List;

public class Student {

    String firstName;
    String lastName;
    List<String> courses;

    public Student(String firstName, String lastName){
        this.firstName=firstName;
        this.lastName=lastName;
    }

    public String getFullName(){
        return firstName+" "+lastName;
    }

}
