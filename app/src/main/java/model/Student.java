package model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Student {

    private List<Course> courses;
    private List<String> chatRooms;
    private List<Session> sessions;
    private List<String> matched;
    private List<String> passed;
    private List<String> pendingMatches;
    private String id;

    //public Student(String foo, String bar){ } // TODO: DELETE THIS CONSTRUCTOR

    public Student(String id){
        this.id = id;
        this.populateStudentInfo();
        //TODO: Retrieve student's courses, email, chatRooms, sessions, matched, passed, status from DB

    }

    public void populateStudentInfo(){
        courses = new ArrayList<Course>();
        chatRooms = new ArrayList<String>();
        sessions = new ArrayList<Session>();
        matched = new ArrayList<String>();
        passed = new ArrayList<String>();
        pendingMatches = new ArrayList<String>();
        //TODO: IMPLEMENT
    }

    public String getEmail(){
        return null;
    }

    public List<String> getChatRooms(){
        return chatRooms;
    }

    public void addChatRoom(String room){
        chatRooms.add(room);
        //TODO: IMPLEMENT
    }

    public void removeChatRoom(String room) {
        //TODO: IMPLEMENT
    }

    public List<Session> getSessions(){
        return sessions;
    }

    public List<Course> getCourses(){
        return courses;
    }

    public void addCourse(Course course) {
        courses.add(course);
        return;  //TODO: IMPLEMENT FOR COURSE ACTIVITY
    }

    public void removeCourse(Course course) {
        return;  //TODO: IMPLEMENT FOR COURSE ACTIVITY
    }

    public List<String> getMatched(){
        return matched;
    }

    public void addMatchedStudent(String matchedStudentID){
        matched.add(matchedStudentID);
        //TODO: IMPLEMENT
    }

    public List<String> getPassed(){
        return passed;
    }

    public void addPassedStudent(String passedStudentID){
        passed.add(passedStudentID);
        //TODO: IMPLEMENT
    }

    public List<String> getPendingMatches(){
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

    public Student deleteAccount(){
        return null;
        //TODO: IMPLEMENT
    }
}
