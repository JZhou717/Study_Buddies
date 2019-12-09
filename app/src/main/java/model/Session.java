package model;

import java.io.Serializable;
import java.util.Date;

public class Session implements Serializable {
    private String partnerID;
    private Date dateTime;
    private int reminder;

    /**
     * Constructor
     * @param partnerID id of study session's partner
     * @param dateTime Date of the study session
     * @param reminder Reminder that the current user sets for the study session
     */
    public Session(String partnerID, Date dateTime, int reminder) {
        this.partnerID = partnerID;
        this.dateTime = dateTime;
        this.reminder = reminder;
    }
    public Date getDateTime() {
        return dateTime;
    }

    public int getReminder() {
        return reminder;
    }

    public String getPartnerID() {
        return partnerID;
    }

}
