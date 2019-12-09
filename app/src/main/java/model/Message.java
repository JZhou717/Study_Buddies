package model;

import java.io.Serializable;

public class Message  implements Serializable {
    private String text;
    private String senderID;


    public Message(String text, String senderID) {
        this.text = text;
        this.senderID=senderID;
    }
    public String getText() {
        return text;
    }

    public String getSenderID() {
        return senderID;
    }
}
