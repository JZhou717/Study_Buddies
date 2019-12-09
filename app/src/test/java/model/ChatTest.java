package model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ChatTest {

    Chat chat;

    Student chattingStudent;

    @Before
    public void setup() {

        chat=new Chat("room1", "1");
        chat.addMessage("Hello", "1");
        chat.addMessage("Hi", "2");
    }

    @Test
    public void testGetLastMessage() {
        Message message=chat.getLastMessage();
        assertTrue(message.getSenderID().equals("2"));
        assertTrue(message.getText().equals("Hi"));
    }
    @Test
    public void testGetRoom() {
        assertTrue(chat.getRoom().equals("room1"));
    }
    @Test
    public void testGetMessages() {
        List<Message> messages=chat.getMessages();
        Message message1=messages.get(0);
        Message message2=messages.get(1);

        assertTrue(message1.getText().equals("Hello"));
        assertTrue(message1.getSenderID().equals("1"));
        assertTrue(message2.getText().equals("Hi"));
        assertTrue(message2.getSenderID().equals("2"));
    }
    @Test
    public void testAddMessage() {
        chat.addMessage("Study", "1");
        assertTrue(chat.getMessages().size()==3);
    }

    @Test
    public void testGetSessionRequestMessageID() {
        assertTrue(chat.getSessionRequestMessageID().equals("Session "+chat.getRoom()));
    }
}