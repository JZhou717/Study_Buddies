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

    private Button blockButton;
    private Button studyButton;
    //Need this for our drawer layout
    private DrawerLayout drawer;

    private Student currentUser=BindrController.getCurrentUser();
    private Student chattingStudent;
    private String channelID = "EgTZXDSK6J2eOp6U";
    private EditText editText;
    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    private Chat chat=null;
    private String firstMessage=null;
    private String requestedSessionMessage=null;

    DialogInterface.OnClickListener blockClickListener;
    DialogInterface.OnClickListener sessionRequestListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbox);
        /* Start Navigation Stuff */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        studyButton=findViewById(R.id.studyButton);
        blockButton=findViewById(R.id.blockButton);
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

        //get chats class
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        scaledrone = new Scaledrone(channelID, currentUser.getId());

        if(bundle.getString("type").equals("chat")){
            chat=(Chat)bundle.getSerializable("Chat");
            getSupportActionBar().setTitle(chat.getChattingStudentFullName());
            loadHistoryMessages();
            //initial connection
            scaledrone.connect(new ScaledroneListener());
            if(bundle.containsKey("Session")){
                Session session=(Session)bundle.get("Session");
                Date dateTime=session.getDateTime();
                Calendar calendar=Calendar.getInstance();
                calendar.setTime(dateTime);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH)+1;
                int date = calendar.get(Calendar.DATE);
                String dateText=month + "/" + date + "/" + year;
                String timeText = DateFormat.format("h:mm a", calendar).toString();

                requestedSessionMessage="Study Session Request \nDate: "+dateText+"\nTime: "+timeText;
                chat.requestSession(new DatabaseCallBack<String>() {
                    @Override
                    public void onCallback(String items) {
                        scaledrone.publish(chat.getRoom(), chat.getSessionRequestMessage());

                        scaledrone.publish(chat.getRoom(), requestedSessionMessage);
                        chat.saveMesssage(currentUser.getId(), requestedSessionMessage);
                    }
                }, currentUser.getId(), session.getDateTime(), session.getReminder());



            }


        }else {
            studyButton.setEnabled(false);
            studyButton.setAlpha(0.5f);
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

        blockClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(ChatboxActivity.this, "Successfully Blocked", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChatboxActivity.this, ChatsListActivity.class);
                        startActivity(intent);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };
        /*sessionRequestListener= new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(ChatboxActivity.this, "Successfully Set Up Study Session", Toast.LENGTH_LONG).show();
                        String acceptMessage="Study Session Accepted";
                        scaledrone.publish(chat.getRoom(), acceptMessage);
                        chat.saveMesssage(currentUser.getId(), acceptMessage);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(ChatboxActivity.this, "Study Session is Cancelled", Toast.LENGTH_LONG).show();
                        String declineMessage="Study Session Declined";
                        scaledrone.publish(chat.getRoom(), declineMessage);
                        chat.saveMesssage(currentUser.getId(), declineMessage);
                        break;
                }
            }
        };*/

        //Chatbox code
        //get user input
        editText = (EditText) findViewById(R.id.editText);

        messageAdapter = new MessageAdapter(this, chat.getChattingStudentID());
        messagesView = (ListView) findViewById(R.id.messages_view);
        //To display items in the list, need to associate an adapter with the list.
        messagesView.setAdapter(messageAdapter);



    }

    public void sendMessage(View view) {

        String message = editText.getText().toString();
        if (message.length() > 0) {
            if(chat.getRoom()==null){
                studyButton.setEnabled(true);
                studyButton.setAlpha(1f);

                Chat.getNewRoomAssignment(new DatabaseCallBack<String>() {
                    @Override
                    public void onCallback(String item) {
                        chat.setRoom(item);
                        currentUser.saveChatRoom(item, chattingStudent.getId());
                        chattingStudent.saveChatRoom(item, currentUser.getId());

                        //initial connection
                        System.out.println("connecting");
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
    //chatbox
    @Override
    public void onOpen(Room room) {
        System.out.println("Connected to room");
        if(firstMessage!=null) {
            System.out.println("publishing NEW message in new chat");
            scaledrone.publish(chat.getRoom(), firstMessage);
            firstMessage=null;
        }

    }

    @Override
    public void onOpenFailure(Room room, Exception ex) {
        System.out.println("Failed to open connection: " + ex.getMessage());

    }

    @Override
    public void onMessage(Room room, Message receivedMessage) {
        // parse as string
        System.out.println("Message client : "+receivedMessage.getClientID()+" "+receivedMessage.getMember()+" " + receivedMessage.getData().asText());
        boolean belongsToCurrentUser = receivedMessage.getClientID().equals(scaledrone.getClientID());
        String studentID=currentUser.getId();
        System.out.println("Message sender "+receivedMessage.getClientID()+ "current client  "+scaledrone.getClientID());
        String messageString=receivedMessage.getData().asText();
        if (!belongsToCurrentUser){
            studentID=chat.getChattingStudentID();
            if(messageString.equals(chat.getSessionRequestMessage()) ){
                System.out.println("SESSION REQUEST FROM Other");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onSessionRequest(requestedSessionMessage);
                    }
                });
                return;

            }
        }
        else if (belongsToCurrentUser&& messageString.equals(chat.getSessionRequestMessage())){
            System.out.println("SESSION REQUEST FROM you");
            return;
        }
        final model.Message message = new model.Message(messageString, studentID);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageAdapter.add(message);
                messagesView.setSelection(messagesView.getCount() - 1);
            }
        });


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


    public void onSessionRequest(String message) {
        /*final EditText input = new EditText(this);
        input.setHint("reminder (minutes)");
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatboxActivity.this);
        builder.setTitle("From "+chat.getChattingStudentFullName());
        builder.setMessage(message).setPositiveButton("Accept", sessionRequestListener)
                .setNegativeButton("Decline", sessionRequestListener).show();*/

        Bundle bundle = new Bundle();
        bundle.putString(SetSessionDialogFrag.MESSAGE_KEY, message);
        String title="From "+chat.getChattingStudentFullName();
        bundle.putString(SetSessionDialogFrag.TITLE, title);
        DialogFragment newFragment = new SetSessionDialogFrag();
        newFragment.setCancelable(false);
        newFragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(newFragment, "sessionRequest");
        ft.commitAllowingStateLoss();

        //newFragment.show(getSupportFragmentManager(), "sessionRequest");

    }

    public void onBlockClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatboxActivity.this);
        builder.setMessage("Are you sure you want to block? This cannot be undone").setPositiveButton("Yes", blockClickListener)
                .setNegativeButton("No", blockClickListener).show();
    }

    public void onStudySessionClick(View view) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("Chat", chat);

        Intent intent = new Intent(ChatboxActivity.this, SetStudySessionActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);

    }

    private void loadHistoryMessages(){
        chat.findMessages(new DatabaseCallBack<List<Document>>() {
            @Override
            public void onCallback(List<Document> items) {
                for (Document item: items) {
                    Log.d("app", String.format("successfully found:  %s", item.toString()));
                    String sender=item.get("sender").toString();
                    String text=item.getString("text");
                    System.out.println("MESSAGE HISTORY "+sender +" "+text );
                    chat.addMessage(text, sender);
                    messageAdapter.add(chat.getLastMessage());
                    messagesView.setSelection(messagesView.getCount() - 1);
                }

            }
        });
    }

    @Override
    public void onPositive(String message) {
        Toast.makeText(ChatboxActivity.this, "Successfully Set Up Study Session", Toast.LENGTH_LONG).show();
        String acceptMessage="Study Session Accepted";
        scaledrone.publish(chat.getRoom(), acceptMessage);
        chat.saveMesssage(currentUser.getId(), acceptMessage);

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
                Date date= (Date) request.get("dateTime");
                int partnerReminder=request.getInteger("reminder");
                setUpStudySession(partnerReminder, reminder, date);

            }
        });


    }

    private void setUpStudySession(int partnerReminder, int reminder, Date date){
        currentUser.addSession(date, reminder, chat.getChattingStudentID());
        chat.getChattingStudent().addSession(date, partnerReminder, currentUser.getId());
    }

    @Override
    public void onNegative(String message) {
        Toast.makeText(ChatboxActivity.this, "Study Session is Cancelled", Toast.LENGTH_LONG).show();
        String declineMessage="Study Session Declined";
        scaledrone.publish(chat.getRoom(), declineMessage);
        chat.saveMesssage(currentUser.getId(), declineMessage);
    }

    private class ScaledroneListener implements Listener{

        @Override
        public void onOpen() {
            System.out.println("Scaledrone connection open");
            Room room=scaledrone.subscribe(chat.getRoom(), ChatboxActivity.this,new SubscribeOptions(100));

        }

        @Override
        public void onOpenFailure(Exception ex) {
            System.out.println("Scaledrone connection open failure");
            System.err.println(ex);
        }

        @Override
        public void onFailure(Exception ex) {
            System.out.println("Scaledrone connection failure");
            System.err.println(ex);
            System.out.println("Scaledrone reconnecting");
            scaledrone.connect(new ScaledroneListener());
        }

        @Override
        public void onClosed(String reason) {
            System.out.println("Scaledrone connection closed");
            System.err.println(reason);
        }


    }

}
