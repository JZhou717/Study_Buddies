package model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteDeleteResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteFindOneAndModifyOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteFindOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;
import com.study.bindr.BindrController;
import com.study.bindr.DatabaseCallBack;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Chat implements Serializable {


    private String room="";
    private ArrayList<Message> messages;
    private String chattingStudentFullName;
    private Student chattingStudent;

    /**
     * Constructor that takes in room and the chatting student's id.
     * This constructor is mainly used when the current user and the other student has previously chatted.
     * @param room the chatroom that the current user and chatting student are sending messages in (a unique string id).
     * @param chattingStudentID the chatting student's id equal to the object id in the database
     */
    public Chat(String room, String chattingStudentID) {
        this.room = room;
        this.chattingStudent=new Student(chattingStudentID);
        this.messages=new ArrayList<Message>();
    }

    /**
     * Constructor that takes in a student object.
     * This constructor is mainly used when the current user and the other student has not chatted before, and therefore has not charoom.
     * @param chattingStudent
     */
    public Chat(Student chattingStudent){
        this.chattingStudent=chattingStudent;
        this.messages=new ArrayList<Message>();

    }

    /**
     * Get the last message sent from list
     * @return The last message in the list
     */
    public Message getLastMessage(){

        return messages.get(messages.size()-1);

    }

    /**
     * When a student requests a study session, we send a message containing this room
     * @return String containing "Session [room]"
     */
    public String getSessionRequestMessageID(){
        return "Session "+room;
    }

    /**
     * Gets the last message sent in this chat room from the database.
     * @param dbCallBackResult The method to which the message document is passed to
     */
    public void findLastMessage(DatabaseCallBack<Document> dbCallBackResult){
        Document query = new Document().append("room", this.room);

        Document projection = new Document()
                .append("_id", 0)
                .append("messages", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findLastMessage = BindrController.chatsCollection.findOne(query, options);
        findLastMessage.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("findLastMessage", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("findLastMessage", String.format("Successfully found document: %s",
                            task.getResult()));
                    Document item = task.getResult();
                    List<Document> messages= (List<Document>) item.get("messages");
                    Document message=messages.get(messages.size()-1);

                    dbCallBackResult.onCallback(message);

                } else {
                    Log.e("findLastMessage", "Failed to findOne: ", task.getException());
                }
            }
        });


    }

    /**
     * Saves the message and the sender to the chat document with this room in the database.
     * @param senderID the message sender's id
     * @param message the chat message to be saved
     */
    public void saveMesssage(String senderID, String message) {

        Document filterDoc = new Document().append("room", room);
        Document updateDoc = new Document().append("$push",
                new Document().append("messages",
                        new Document().append("sender", new ObjectId(senderID))
                                .append("text", message))
        );

        RemoteUpdateOptions options = new RemoteUpdateOptions().upsert(true);

        final Task<RemoteUpdateResult> saveMessageTask =
                BindrController.chatsCollection.updateOne(filterDoc, updateDoc, options);
        saveMessageTask.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("saveMesssage", String.format("successfully matched %d and modified %d documents",
                            numMatched, numModified));
                    Log.d("saveMesssage", String.format("Message SAVED"));
                } else {
                    Log.e("saveMesssage", "failed to save message ", task.getException());
                }
            }
        });
    }

    /**
     * Creates a new chat document in the database with the given room name.
     * Also saves the first message sent to the room.
     * @param senderID the message sender's id
     * @param message the chat message to be saved
     * @param room the room to save the chat message and sender in
     */
    public void saveNewChatMessage(String senderID, String message, String room){
        Document newChat = new Document()
                .append("room", room)
                .append("senders", Arrays.asList(new ObjectId(senderID), new ObjectId(chattingStudent.getId())))
                .append("messages", Arrays.asList(new Document().append("sender", new ObjectId(senderID))
                                                .append("text", message)));


        final Task <RemoteInsertOneResult> insertNewChat = BindrController.chatsCollection.insertOne(newChat);
        insertNewChat.addOnCompleteListener(new OnCompleteListener <RemoteInsertOneResult> () {
            @Override
            public void onComplete(@NonNull Task <RemoteInsertOneResult> task) {
                if (task.isSuccessful()) {
                    Log.d("saveNewChatMessage", String.format("successfully inserted item with id %s",
                            task.getResult().getInsertedId()));
                    Log.d("saveNewChatMessage", String.format("Message SAVED"));

                } else {
                    Log.e("saveNewChatMessage", "failed to insert document with: ", task.getException());
                }
            }
        });
    }

    /**
     * gets the room of this chat
     * @return this chat's room
     */
    public String getRoom() {
        return room;
    }

    /**
     * Sets this chat's room name
     * @param room the room to set the chat to.
     */
    public void setRoom(String room){
        this.room=room;
    }

    /**
     * get the chatting student's id
     * @return id of the chatting student
     */
    public String getChattingStudentID(){
        return this.chattingStudent.getId();
    }

    /**
     * set the full name of the chatting student
     * @param fullName the name to set the chatting student
     */
    public void setChattingStudentFullName(String fullName){
        this.chattingStudentFullName=fullName;
    }

    /**
     * get the chatting student's full name
     * @return the full name of the chatting student
     */
    public String getChattingStudentFullName(){
        return this.chattingStudentFullName;
    }

    /**
     * Get the chatting student Student Object
     * @return Student Object of the chatting student
     */
    public Student getChattingStudent(){
        return this.chattingStudent;
    }

    /**
     * Get all the messages in this chat
     * @return list of Message Objects in this chat
     */
    public ArrayList<Message> getMessages() {
        return messages;
    }


    /**
     * Finds the previous messages of this chat from database
     * @param databaseCallBack The method to which the list of message documents is passed to
     */
    public void findMessages(DatabaseCallBack<List<Document>> databaseCallBack){
        //Query by chat room
        Document query = new Document().append("room", this.room);
        //Project the messages array
        Document projection = new Document()
                .append("_id", 0)
                .append("messages", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        //listens for when the query finishes and sends result to callback method (given in parameter)
        final Task<Document> findMessages = BindrController.chatsCollection.findOne(query, options);
        findMessages.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("findMessages", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("findMessages", String.format("Successfully found document: %s",
                            task.getResult()));
                    Document item = task.getResult();
                    List<Document> messages= (List<Document>) item.get("messages");
                    databaseCallBack.onCallback(messages);

                } else {
                    Log.e("findMessages", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * Add a message to this chat's message list
     * @param text message
     * @param sender senderID of the message
     */
    public void addMessage(String text, String sender){
        Message message=new Message(text, sender);
        messages.add(message);
    }

    /**
     * To open up a new chat between 2 students, need to assign them to a room number.
     * This method gets an unassigned room number from the database by incrementing the last room number assigned
     * @param databaseCallBack The method to which the room name is passed to
     */
    public static void getNewRoomAssignment(DatabaseCallBack<String> databaseCallBack){
        Document query = new Document().append("roomTrack", new Document().append("$exists", true));

        // Set some fields in that document
        Document update = new Document().append("$inc", new Document().append("roomTrack", 1));

        Document projection = new Document()
                .append("roomTrack", 1);


        RemoteFindOneAndModifyOptions options = new RemoteFindOneAndModifyOptions()
                // Return the updated document instead of the original document
                .returnNewDocument(true)
                .upsert(false)
                .projection(projection);

        final Task <Document> findNewRoom = BindrController.chatsCollection.findOneAndUpdate(query, update, options);
        findNewRoom.addOnCompleteListener(new OnCompleteListener <Document> () {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getNewRoomAssignment", String.format("No document matches the provided query"));
                }
                else if (task.isSuccessful()) {
                    Log.d("getNewRoomAssignment", String.format("Successfully updated document: %s",
                            task.getResult()));
                    String room=task.getResult().getLong("roomTrack").toString();
                    databaseCallBack.onCallback("room"+room);
                } else {
                    Log.e("getNewRoomAssignment", "Failed to findOneAndUpdate: ", task.getException());
                }
            }
        });

    }

    /**
     * Saves a request document into this chat's document in database
     * @param databaseCallBack The method to which the room name is passed to
     * @param senderID the ID of the student who requested the session
     * @param dateTime time of the study session
     * @param reminder reminder for the study session
     */
    public void requestSession(DatabaseCallBack<String> databaseCallBack, String senderID, Date dateTime, int reminder){

        Document filterDoc = new Document().append("room", room);
        Document updateDoc = new Document().append("$set", new Document()
                .append("request", new Document()
                        .append("sender", new ObjectId(senderID))
                        .append("datetime", dateTime)
                        .append("reminder", reminder)));
        RemoteUpdateOptions options = new RemoteUpdateOptions().upsert(true);

        final Task<RemoteUpdateResult> requestSessionTask =
                BindrController.chatsCollection.updateOne(filterDoc, updateDoc, options);
        requestSessionTask.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("requestSession", String.format("successfully matched %d and modified %d documents",
                            numMatched, numModified));
                    databaseCallBack.onCallback("ok");
                } else {
                    Log.e("requestSession", "failed to save request ", task.getException());
                }
            }
        });
    }

    /**
     * gets the request document from this chat document in database.
     * Sends back null if no requests are found.
     * @param databaseCallBack The method to which the request document is passed to
     */
    public void getRequestedSession(DatabaseCallBack<Document> databaseCallBack){

        Document query = new Document().append("room", this.room).append("request", new Document("$exists", true));

        Document projection = new Document()
                .append("_id", 0)
                .append("request", 1);

        RemoteFindOptions options = new RemoteFindOptions()
                .projection(projection);

        final Task<Document> findRequest = BindrController.chatsCollection.findOne(query, options);
        findRequest.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task <Document> task) {
                if (task.getResult() == null) {
                    Log.d("getRequestedSession", String.format("No document matches the provided query"));
                    databaseCallBack.onCallback(null);
                }
                else if (task.isSuccessful()) {
                    Log.d("getRequestedSession", String.format("Successfully found document: %s",
                            task.getResult()));
                    Document item = (Document) task.getResult().get("request");

                    databaseCallBack.onCallback(item);

                } else {
                    Log.e("getRequestedSession", "Failed to findOne: ", task.getException());
                }
            }
        });
    }

    /**
     * Removes this chat's session request from this chat's document in the database
     */
    public void removeRequest() {
        Document filterDoc = new Document().append("room", room);
        Document updateDoc = new Document().append("$unset", new Document()
                .append("request", 1));
        RemoteUpdateOptions options = new RemoteUpdateOptions().upsert(true);

        final Task<RemoteUpdateResult> removeRequest =
                BindrController.chatsCollection.updateOne(filterDoc, updateDoc, options);
        removeRequest.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                if (task.isSuccessful()) {
                    long numMatched = task.getResult().getMatchedCount();
                    long numModified = task.getResult().getModifiedCount();
                    Log.d("removeRequest", String.format("successfully matched %d and modified %d documents",
                            numMatched, numModified));
                } else {
                    Log.e("removeRequest", "failed to remove request ", task.getException());
                }
            }
        });
    }

    /**
     * Removes the chat document that contains this chat's room name from the database
     */
    public void removeChat(){
        //find the chat with
        Document query = new Document()
                .append("room", this.room);

        final Task<RemoteDeleteResult> deleteTask = BindrController.chatsCollection.deleteOne(query);
        deleteTask.addOnCompleteListener(new OnCompleteListener <RemoteDeleteResult> () {
            @Override
            public void onComplete(@NonNull Task <RemoteDeleteResult> task) {
                if (task.isSuccessful()) {
                    long numDeleted = task.getResult().getDeletedCount();
                    Log.d("removeChat", String.format("successfully deleted %d documents", numDeleted));
                } else {
                    Log.e("removeChat", "Failed to deleted documents ", task.getException());
                }
            }
        });

    }
}
