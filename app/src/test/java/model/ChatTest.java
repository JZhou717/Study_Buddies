package model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ChatTest {

    Chat chat;
    Message message1;
    Message message2;
    ArrayList<Message> messages;
    Student chattingStudent;

    @Before
    public void setup() {
        message1=new Message("Hello", "1");
        message2=new Message("Hi", "1");
        messages=new ArrayList<Message>();
        messages.add(message1);
        messages.add(message2);

        chat=new Chat("room1", "1");
    }

    @Test
    public void testGetLastMessage() {

        assertTrue(chat.getLastMessage().equals(message2));
    }
    @Test
    public void testGetRoom() {
        assertTrue(chat.getRoom().equals("room1"));
    }
    @Test
    public void testGetMessages() {
        assertTrue(chat.getMessages().equals(messages));
    }
    @Test
    public void testAddMessage() {
        chat.addMessage("Study", "1");
        assertTrue(messages.size()==3);
    }

    @Test
    public void testGetSessionRequestMessageID() {
        assertTrue(chat.getSessionRequestMessageID().equals("Session "+chat.getRoom()));
    }
}