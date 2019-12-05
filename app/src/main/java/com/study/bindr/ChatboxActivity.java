package com.study.bindr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.material.navigation.NavigationView;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Message;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;
import com.scaledrone.lib.SubscribeOptions;

import org.bson.Document;

import java.util.List;

import model.Chat;
import model.Session;
import model.Student;

public class ChatboxActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RoomListener {

    private Button blockButton;
    private Button studyButton;
    //Need this for our drawer layout
    private DrawerLayout drawer;

    //CURRENT STUDENT TEST
    private String id="5ddc5d142b665e671c7ff7bd";
    private Student me=new Student(id);
    private Student chattingStudent;
    private String channelID = "EgTZXDSK6J2eOp6U";
    private EditText editText;
    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    private Chat chat=null;
    private String firstMessage=null;

    DialogInterface.OnClickListener blockClickListener;

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

        scaledrone = new Scaledrone(channelID);


        if(bundle.getString("type").equals("chat")){
            chat=(Chat)bundle.getSerializable("Chat");
            getSupportActionBar().setTitle(chat.getChattingStudentFullName());
            loadHistoryMessages();
            //initial connection
            scaledrone.connect(new ScaledroneListener());
            if(bundle.containsKey("Session")){
                Session session=(Session)bundle.get("Session");
                me.addSession(session);
                chat.requestSession(new DatabaseCallBack<String>() {
                    @Override
                    public void onCallback(String items) {
                        scaledrone.publish(chat.getRoom(), "Session: "+chat.getRoom());;
                    }
                }, me.getId());
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
                        me.saveChatRoom(item, chattingStudent.getId());
                        chattingStudent.saveChatRoom(item, me.getId());

                        //initial connection
                        System.out.println("connecting");
                        scaledrone.connect(new ScaledroneListener());
                        firstMessage=message;
                        chat.saveNewChatMessage(me.getId(), message, chat.getRoom());

                    }

                });


            }else{
                scaledrone.publish(chat.getRoom(), message);
                chat.saveMesssage(id, message);


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
        final model.Message message = new model.Message(receivedMessage.getData().asText(), "1" );
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


    public void onBlockClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatboxActivity.this);
        builder.setMessage("Are you sure you want to block?").setPositiveButton("Yes", blockClickListener)
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
