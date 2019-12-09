package model;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {


    Message message;

    @Before
    public void setup() {
        message=new Message("Hello", "1");
    }

    @Test
    public void testGetText() {

        assertTrue(message.getText().equals("Hello"));
    }

    @Test
    public void testGetSenderID() {
        assertTrue(message.getSenderID()=="1");
    }

}