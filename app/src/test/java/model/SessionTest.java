package model;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.Assert.*;


public class SessionTest {

    private Session session;
    private final String sampleID = "test";
    private final Date dateTime = new Date();
    private final int reminder = 0;


    @Before
    public void setup(){
        session = new Session(sampleID,dateTime,reminder);
    }

    @Test
    public void testGetPartnerID(){
        assertTrue(sampleID.equals(session.getPartnerID()));
    }

    @Test
    public void testGetDateTime(){
        assertTrue(dateTime.equals(session.getDateTime()));
    }

    @Test
    public void testGetReminder(){
        assertTrue(reminder == session.getReminder());
    }
}

