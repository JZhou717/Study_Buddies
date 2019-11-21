package model;

import java.util.List;

public class Student {

    private List<Course> courses;
    private List<String> chatRooms;
    private List<Session> sessions;
    private List<Integer> matched;
    private List<Integer> passed;
    private List<Integer> pendingMatches;
    private int id;

    public Student(String foo, String bar){ } // TODO: DELETE THIS CONSTRUCTOR

    public Student(int id){
        this.id = id;
        //TODO: Retrieve student's courses, chatRooms, sessions, matched, passed from DB
    }

    public String getEmail(){
        return null;
    }

    public List<String> getChatRooms(){
        return chatRooms;
    }

    public List<Session> getSessions(){
        return sessions;
    }

    public List<Course> getCourses(){
        return courses;
    }

    public List<Integer> getMatched(){
        return matched;
    }

    public List<Integer> getPassed(){
        return passed;
    }

    public List<Integer> getPendingMatches(){
        return pendingMatches;
    }

    public String getUsername(){
        return null;  //TODO: GET FROM DB
    }

    public String getPassword(){
        return null;  //TODO: GET FROM DB
    }

    public String getFullName(){
        return null;  //TODO: GET FROM DB
    }

    public String getBio(){
        return null; //TODO: GET FROM DB
    }

    public double getGPA(){
        return -1;  //TODO: GET FROM DB
    }

    public void addChatRoom(String room){
        //TODO: IMPLEMENT
    }

    public void editEmail(String newEmail){
        //TODO: IMPLEMENT
    }

    public void editPassword(String newPassword){
        //TODO: IMPLEMENT
    }

    public void editName(String newName){
        //TODO: IMPLEMENT
    }

    public void editBio(String newBio){
        //TODO: IMPLEMENT
    }

    public void editGPA(double newGPA){
        //TODO: IMPLEMENT
    }

    public void setStatus(boolean isActive){
        //TODO: IMPLEMENT
    }

    public void deleteAccount(){
        //TODO: IMPLEMENT
    }

    public void populateStudentInfo(){
        //TODO: IMPLEMENT
    }

    public void addPassedStudent(int passedStudentID){
        //TODO: IMPLEMENT
    }

    public void addMatchedStudent(int matchedStudentID){
        //TODO: IMPLEMENT
    }
}
