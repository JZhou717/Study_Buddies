package model;

import java.io.Serializable;

public class Message  implements Serializable {
    private String text;
    private String senderID;


    private boolean requestSession=false;

    public Message(String text, String senderID) {
        this.text = text;
        this.senderID=senderID;
    }
    public Message(String text, String senderID, boolean requestSession) {
        this.text = text;
        this.senderID=senderID;
        this.requestSession=requestSession;
    }
    public String getText() {
        return text;
    }

    public String getSenderID() {
        return senderID;
    }
    public boolean isRequestSession() {
        return requestSession;
    }
}
