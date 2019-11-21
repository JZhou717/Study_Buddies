package model;

import java.util.ArrayList;

public class Chat {

    private String room;
    private ArrayList<Message> messages;

    public Chat(String room, ArrayList<Message> messages) {
        this.room = room;
        this.messages = messages;
    }

    public Message getLastMessage(){

        return messages.get(messages.size()-1);

    }
    public String getRoom() {
        return room;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }
    public void addMessage(String text, int sender){
        Message message=new Message(text, sender);
        messages.add(message);
    }

}
