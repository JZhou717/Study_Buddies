package model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.BasicDBObject;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteFindOptions;
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
    private String chattingStudentID;
    private String chattingStudentFullName;

    public Chat(String room) {
        this.room = room;
        this.messages=new ArrayList<Message>();
    }

    public Chat(){
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


    public void saveMesssage(DatabaseCallBack dbCallBack, String room, String senderID, String message) {

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
                    Log.d("app", String.format("successfully matched %d and modified %d documents",
                            numMatched, numModified));
                    dbCallBack.onCallback("ok");
                } else {
                    Log.e("app", "failed to update document with: ", task.getException());
                }
            }
        });
    }

        public String getRoom() {
        return room;
    }
    public String getChattingStudentID(){
        return this.chattingStudentID;
    }
    public void setChattingStudentID(String id){
        this.chattingStudentID=id;
    }
    public void setChattingStudentFullName(String fullName){
        this.chattingStudentFullName=fullName;
    }
    public String getChattingStudentFullName(){
        return this.chattingStudentFullName;
    }

    /**
     * Finds the other student's id and full_name from database given the chatroom and the current user id
     */
    public void findChattingStudent(DatabaseCallBack<Document> dbCallBack, String currentStudentID){
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

    }
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

}
