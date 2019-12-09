package com.study.bindr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Message;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;
import com.scaledrone.lib.SubscribeOptions;

import org.bson.Document;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.Chat;
import model.Session;
import model.Student;

public class ChatboxActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RoomListener, DialogReturn{
    //Need this for our drawer layout
    private DrawerLayout drawer;

    private Button blockButton;
    private Button studyButton;

    //Channel ID for Scaledrone
    private String channelID = "EgTZXDSK6J2eOp6U";
    private Scaledrone scaledrone;
    private EditText editText;

    private MessageAdapter messageAdapter;
    private ListView messagesView;

    //Stores the first message sent between current user and chatting student
    private String firstMessage=null;
    private Chat chat=null;
    private Student currentUser=BindrController.getCurrentUser();
    private Student chattingStudent;

    DialogInterface.OnClickListener blockClickListener;

    /**
     * displays activity, runs code for startup.
     * Creates navigation bar, displays previous messages (if any), and sets up listeners
     * @param savedInstanceState -bundle passed by previous activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chatbox);

        /* Start Navigation Stuff */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Change the view to the proper screen
        drawer = findViewById(R.id.chatbox_screen);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Change this to the proper page
        navigationView.getMenu().getItem(0).setChecked(false);
        /* End Navigation Stuff */

        studyButton=findViewById(R.id.studyButton);
        blockButton=findViewById(R.id.blockButton);

        //Get data from chats list activity
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        //Create new scaledrone client for message sending.
        scaledrone = new Scaledrone(channelID, currentUser.getId());

        //Current user and student has chatted before
        if(bundle.getString("type").equals("chat")){
            //Get the Chat object that was selected in the Chats list activity
            chat=(Chat)bundle.getSerializable("Chat");
            chattingStudent=chat.getChattingStudent();
            getSupportActionBar().setTitle(chat.getChattingStudentFullName());
            loadHistoryMessages();

            //Initial connection
            scaledrone.connect(new ScaledroneListener());

            //See if current user got any session Requests
            checkSessionRequest();

            //Check if current user requested a session
            if(bundle.containsKey("Session")){

                Session session=(Session)bundle.get("Session");
                Date dateTime=session.getDateTime();
                //Save request in database
                chat.requestSession(new DatabaseCallBack<String>() {
                    @Override
                    public void onCallback(String items) {

                        String requestedSessionMessage="Study Session Request \n"+convertDateTimeToString(dateTime);
                        //sends session request ID to student so they will know a session has been requested
                        scaledrone.publish(chat.getRoom(), chat.getSessionRequestMessageID());
                        //Sends the request information to student
                        scaledrone.publish(chat.getRoom(), requestedSessionMessage);
                        //Save the request information in chat database
                        chat.saveMesssage(currentUser.getId(), requestedSessionMessage);
                    }
                }, currentUser.getId(), session.getDateTime(), session.getReminder());
            }
        }else {//Current user and student never chatted before
            //Can only set up study session after 1st message
            studyButton.setEnabled(false);
            studyButton.setAlpha(0.5f);
            //Get the chatting student object and create a chat object with no associated room name
            chattingStudent= (Student) bundle.get("Student");
            chat = new Chat(chattingStudent);
            chattingStudent.getFullName(new DatabaseCallBack<String>() {
                @Override
                public void onCallback(String item) {
                    chat.setChattingStudentFullName(item);
                    getSupportActionBar().setTitle(chat.getChattingStudentFullName());
                }
            });
        }

        //Listens for when user clicks block button. Removes chatting student from current user's matches list
        blockClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Remove from each other's chats list
                        currentUser.removeChatRoom(chat.getRoom());
                        chat.getChattingStudent().removeChatRoom(chat.getRoom());

                        //remove chat document that has all the messages
                        chat.removeChat();

                        //add to each other's passed list
                        currentUser.addPassedStudent(chat.getChattingStudentID());
                        chat.getChattingStudent().addPassedStudent(currentUser.getId());

                        //remove from each other's matched list
                        currentUser.removeMatchedStudent(chat.getChattingStudentID());
                        chat.getChattingStudent().removeMatchedStudent(currentUser.getId());

                        Toast.makeText(ChatboxActivity.this, "Successfully Blocked", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChatboxActivity.this, ChatsListActivity.class);
                        startActivity(intent);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        //Chatbox code
        //get user input
        editText = (EditText) findViewById(R.id.editText);

        messageAdapter = new MessageAdapter(this, chat.getChattingStudentID());
        messagesView = (ListView) findViewById(R.id.messages_view);
        //To display items in the list, need to associate an adapter with the list.
        messagesView.setAdapter(messageAdapter);



    }


    /* Start Navigation Stuff */
    //Navbar closes on activity change
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent=new Intent(ChatboxActivity.this, ChatsListActivity.class);

            startActivity(intent);
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {
            case R.id.nav_home:
                intent = new Intent(ChatboxActivity.this, Home_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                intent = new Intent(ChatboxActivity.this, UserProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_courses:
                intent = new Intent(ChatboxActivity.this, EditCoursesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_session:
                intent = new Intent(ChatboxActivity.this, Session_Activity.class);
                startActivity(intent);
                break;
            case R.id.nav_chatslist:
                intent = new Intent(ChatboxActivity.this, ChatsListActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                intent = new Intent(ChatboxActivity.this, Bindr.class);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /* End Navigation Stuff */


    /**
     * Converts Date to String (Ex: Date: 11/07/2019 Time: 4:29 PM)
     * @param dateTime Date object to be converted to string message.
     * @return String message of given Date object.
     */
    private String convertDateTimeToString(Date dateTime){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(dateTime);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int date = calendar.get(Calendar.DATE);
        String dateText=month + "/" + date + "/" + year;
        String timeText = DateFormat.format("h:mm a", calendar).toString();

        String dateTimeString="Date: "+dateText+"\nTime: "+timeText;
        return dateTimeString;
    }

    /**
     * Adds a new chat document if current user and student never chatted before.
     * Saves the message to database, and sends it to the student using Scaledrone.
     * @param view
     */
    public void sendMessage(View view) {
        //get the messsage
        String message = editText.getText().toString();

        if (message.length() > 0) {
            //Never chatted before
            if(chat.getRoom()==null ||chat.getRoom().equals("") ){
                studyButton.setEnabled(true);
                studyButton.setAlpha(1f);

                //Get a new chat room name
                Chat.getNewRoomAssignment(new DatabaseCallBack<String>() {
                    @Override
                    public void onCallback(String item) {
                        chat.setRoom(item);
                        currentUser.saveChatRoom(item, chattingStudent.getId());
                        chattingStudent.saveChatRoom(item, currentUser.getId());

                        //initial connection
                        scaledrone.connect(new ScaledroneListener());
                        firstMessage=message;

                        chat.saveNewChatMessage(currentUser.getId(), message, chat.getRoom());
                    }
                });
            }else{
                scaledrone.publish(chat.getRoom(), message);
                chat.saveMesssage(currentUser.getId(), message);
            }
            editText.getText().clear();
        }
    }

    /**
     * Runs when Scaledrone's connection is opened.
     * If current user sent a message to a student they never chatted with before,
     * onOpen will send this message after connection is established
     * @param room chat room that Scaledrone is subscribed to.
     */
    @Override
    public void onOpen(Room room) {
        //System.out.println("Connected to room");
        if(firstMessage!=null) {
            //System.out.println("publishing new message in new chat");
            scaledrone.publish(chat.getRoom(), firstMessage);
            firstMessage=null;
        }
    }

    /**
     * Runs when Scaledrone's connection failed to open.
     * @param room chat room that Scaledrone is subscribed to.
     * @param ex exception for failure
     */
    @Override
    public void onOpenFailure(Room room, Exception ex) {
        System.out.println("Failed to open connection: " + ex.getMessage());
    }

    /**
     * Scaledrone client will receive all messages that are published its subscribed room.
     * @param room chat room that Scaledrone is subscribed to.
     * @param receivedMessage message that is received from Scaledrone
     */
    @Override
    public void onMessage(Room room, Message receivedMessage) {
        // parse as string
        //System.out.println("Message client : "+receivedMessage.getClientID()+" "+receivedMessage.getMember()+" " + receivedMessage.getData().asText());

        boolean belongsToCurrentUser = receivedMessage.getClientID().equals(scaledrone.getClientID());

        //initialize sender to current user
        String studentID=currentUser.getId();
        //System.out.println("Message sender "+receivedMessage.getClientID()+ "current client  "+scaledrone.getClientID());

        String messageString=receivedMessage.getData().asText();

        if (!belongsToCurrentUser){
            //Sender of message is chatting student
            studentID=chat.getChattingStudentID();

            //Chatting student sent a session request ID, so current user needs to acknowledge request
            if(messageString.equals(chat.getSessionRequestMessageID()) ){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkSessionRequest();
                    }
                });
                return;
            }
        }
        //current user sent a session request ID, so ignore
        else if (belongsToCurrentUser&& messageString.equals(chat.getSessionRequestMessageID())){
            return;
        }

        //Non session request messages are displayed
        final model.Message message = new model.Message(messageString, studentID);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageAdapter.add(message);
                messagesView.setSelection(messagesView.getCount() - 1);
            }
        });
    }

    /**
     * Checks to see if current user has any pending session requests.
     * If so, displays dialog to accept or decline.
     */
    public void checkSessionRequest() {
        chat.getRequestedSession(new DatabaseCallBack<Document>() {
            @Override
            public void onCallback(Document request) {
                if (request!=null){
                    //Get request sender
                    String sessionSender=request.get("sender").toString();

                    //Displays popup if the session request sender is not current user
                    if (!sessionSender.equals(currentUser.getId())){
                        Date dateTime=request.getDate("datetime");
                        String dialogMessage=convertDateTimeToString(dateTime);
                        String title="Session Request From "+chat.getChattingStudentFullName();

                        Bundle bundle = new Bundle();
                        bundle.putString(SetSessionDialogFrag.MESSAGE_KEY, dialogMessage);
                        bundle.putString(SetSessionDialogFrag.TITLE, title);
                        DialogFragment newFragment = new SetSessionDialogFrag();
                        newFragment.setCancelable(false);
                        newFragment.setArguments(bundle);

                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.add(newFragment, "sessionRequest");
                        ft.commitAllowingStateLoss();
                    }
                }
            }
        });
    }

    /**
     * Runs when user clicks on block button. Displays popup to confirm user's actions.
     * @param view
     */
    public void onBlockClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatboxActivity.this);
        builder.setMessage("Are you sure you want to block? This cannot be undone").setPositiveButton("Yes", blockClickListener)
                .setNegativeButton("No", blockClickListener).show();
    }

    /**
     * Runs when user clicks on study button.
     * If there are no pending session requests, will take user to set up session page.
     * @param view
     */
    public void onStudySessionClick(View view) {
        chat.getRequestedSession(new DatabaseCallBack<Document>() {
            @Override
            public void onCallback(Document request) {
                if(request!=null){
                    Toast.makeText(ChatboxActivity.this, "A Study Session has already been sent. Please wait until they respond.", Toast.LENGTH_LONG).show();
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Chat", chat);

                    Intent intent = new Intent(ChatboxActivity.this, SetStudySessionActivity.class);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
            }});
    }

    /**
     * Displays previous messages between current user and chatting student in chatbox.
     */
    private void loadHistoryMessages(){
        chat.findMessages(new DatabaseCallBack<List<Document>>() {
            @Override
            public void onCallback(List<Document> items) {
                for (Document item: items) {
                    String sender=item.get("sender").toString();
                    String text=item.getString("text");
                    //System.out.println("Message from history "+sender +" "+text );
                    chat.addMessage(text, sender);
                    messageAdapter.add(chat.getLastMessage());
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            }
        });
    }

    /**
     * SetSessionDialogFrag callback method for when current user accepts session request.
     * Sends 'Study Session Accepted' and saves it to database.
     * @param message Study Session Accepted
     */
    @Override
    public void onPositive(String message) {

        Toast.makeText(ChatboxActivity.this, "Successfully Set Up Study Session", Toast.LENGTH_LONG).show();
        String acceptMessage="Study Session Accepted";
        scaledrone.publish(chat.getRoom(), acceptMessage);
        chat.saveMesssage(currentUser.getId(), acceptMessage);

        //Saves study session
        chat.getRequestedSession(new DatabaseCallBack<Document>() {
            @Override
            public void onCallback(Document request) {
                if(request==null){
                    return;
                }
                int reminder=0;
                if(!message.equals("")){
                    reminder=Integer.parseInt(message);
                }
                Date date= request.getDate("datetime");
                int partnerReminder=request.getInteger("reminder");
                setUpStudySession(partnerReminder, reminder, date);

                chat.removeRequest();
            }
        });

    }

    /**
     * SetSessionDialogFrag callback method for when current user declines session request.
     * Sends 'Study Session Declined' and saves it to database.
     * @param message Study Session Declined
     */
    @Override
    public void onNegative(String message) {
        Toast.makeText(ChatboxActivity.this, "Study Session is Cancelled", Toast.LENGTH_LONG).show();
        String declineMessage="Study Session Declined";
        scaledrone.publish(chat.getRoom(), declineMessage);
        chat.saveMesssage(currentUser.getId(), declineMessage);
        chat.removeRequest();
    }

    /**
     * Adds study session doc to both current user and chatting student documents in database
     * @param partnerReminder chatting student's reminder time for study session
     * @param reminder current user's reminder time for study session
     * @param date Date of study sessiom
     */
    private void setUpStudySession(int partnerReminder, int reminder, Date date){
        currentUser.addSession(date, reminder, chat.getChattingStudentID());
        chat.getChattingStudent().addSession(date, partnerReminder, currentUser.getId());
    }


    /**
     * Listener for Scaledrone client
     */
    private class ScaledroneListener implements Listener{

        /**
         * Runs when Scaledrone connection is opened
         * Subscribes to a room when Scaledrone connection is opened
         */
        @Override
        public void onOpen() {
            System.out.println("Scaledrone connection open");
            Room room=scaledrone.subscribe(chat.getRoom(), ChatboxActivity.this,new SubscribeOptions(100));

        }

        /**
         * Runs when Scaledrone failed to open room
         * @param ex Exception for failure
         */
        @Override
        public void onOpenFailure(Exception ex) {
            System.out.println("Scaledrone open failure");
            System.err.println(ex);
        }

        /**
         * Runs when Scaledrone failed to connect
         * @param ex Exception for failure
         */
        @Override
        public void onFailure(Exception ex) {
            System.out.println("Scaledrone connection failure");
            System.err.println(ex);
            System.out.println("Scaledrone reconnecting");
            scaledrone.connect(new ScaledroneListener());
        }

        /**
         * Runs when Scaledrone connection has closed
         * @param reason reason for Scaledrone closing
         */
        @Override
        public void onClosed(String reason) {
            System.out.println("Scaledrone connection closed");
            System.err.println(reason);
        }
    }
}
