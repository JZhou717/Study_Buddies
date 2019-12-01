package model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.BasicDBObject;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
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
import java.util.List;

public class Chat implements Serializable {

    private String room;
    private ArrayList<Message> messages;
    private String chattingStudentFullName;
    private Student chattingStudent;

    public Chat(String room, String chattingStudentID) {
        this.room = room;
        this.chattingStudent=new Student(chattingStudentID);
        this.messages=new ArrayList<Message>();
    }

    public Chat(Student chattingStudent){
        this.chattingStudent=chattingStudent;
        this.messages=new ArrayList<Message>();

    }

    public Message getLastMessage(){

        return messages.get(messages.size()-1);

    }

    /**
     * finds the last message in this chat from database
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
    public void saveNewChatMessage(String senderID, String message, String room){
        Document newChat = new Document()
                .append("room", room)
                .append("senders", Arrays.asList(new ObjectId(senderID), new ObjectId(chattingStudent.getId())))
                .append("messages", new Document().append("sender", new ObjectId(senderID))
                                                .append("text", message));


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

    public String getRoom() {
        return room;
    }
    public void setRoom(String room){
        this.room=room;
    }
    public String getChattingStudentID(){
        return this.chattingStudent.getId();
    }
    public void setChattingStudentFullName(String fullName){
        this.chattingStudentFullName=fullName;
    }
    public String getChattingStudentFullName(){
        return this.chattingStudentFullName;
    }
    public Student getChattingStudent(){
        return this.chattingStudent;
    }
    /**
     * Finds the other student's id and full_name from database given the chatroom and the current user id
     */
   /* public void findChattingStudent(DatabaseCallBack<Document> dbCallBack, String currentStudentID){
        //Query by chat room
        Document filterDoc = new Document()
                .append("chats", new BasicDBObject("$in", Arrays.asList(this.room)));
        //Project id and full_name
        RemoteFindIterable findResults = BindrController.studentsCollection
                .find(filterDoc)
                .projection(new Document()
                        .append("_id", 1)
                        .append("full_name", 1));

        //Task finds multiple documents and puts them into an arraylist
        Task<List<Document>> findChattingStudent = findResults.into(new ArrayList<Document>());

        //listens for when the query finishes and calls the given callback method (from parameter)
        findChattingStudent.addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    List<Document> items = task.getResult();
                    Log.d("getChattingStudent", String.format("successfully found %d documents", items.size()));
                    //Go through the documents and find the other student
                    for (Document item: items) {
                        Log.d("getChattingStudent", String.format("successfully found:  %s", item.toString()));

                        if(!item.get("_id").toString().equals(currentStudentID)){

                            System.out.println("Chatting student is "+item.getString("full_name"));
                            //sends document containing id and full_name back
                            dbCallBack.onCallback(item);
                        }
                    }
                } else {
                    Log.e("getChattingStudent", "failed to find documents with: ", task.getException());
                }
            }
        });

    }*/
    public ArrayList<Message> getMessages() {
        return messages;
    }

    /**
     * Finds the previous messages from this chat from database
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
    public void addMessage(String text, String sender){
        Message message=new Message(text, sender);
        messages.add(message);
    }

    /**
     * To open up a new chat between 2 students, need to assign them to a room number.
     * This method gets an unassigned room number from the database by incrementing the last room number assigned
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

}
