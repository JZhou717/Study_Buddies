package model;

public class Message {
    private String text;
    private int senderID;

    public Message(String text, int senderID) {
        this.text = text;
        this.senderID=senderID;
    }

    public String getText() {
        return text;
    }

    public int getSenderID() {
        return senderID;
    }
}
