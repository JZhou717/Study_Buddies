package com.study.bindr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.navigation.NavigationView;
import com.scaledrone.lib.HistoryRoomListener;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Member;
import com.scaledrone.lib.Message;
import com.scaledrone.lib.ObservableRoomListener;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;
import com.scaledrone.lib.SubscribeOptions;

import java.util.ArrayList;
import java.util.Random;

public class ChatboxActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RoomListener {

    private Button blockButton;
    private Button studyButton;
    MemberData data;
    //Need this for our drawer layout
    private DrawerLayout drawer;


    private String channelID = "EgTZXDSK6J2eOp6U";
    private String roomName = "observable-room4";
    private EditText editText;
    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;
    private ListView messagesView;


    DialogInterface.OnClickListener blockClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbox);

        /* Start Navigation Stuff */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messages");

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

        messageAdapter = new MessageAdapter(this, 1);
        messagesView = (ListView) findViewById(R.id.messages_view);
        //To display items in the list, need to associate an adapter with the list.
        messagesView.setAdapter(messageAdapter);

         data = new MemberData(getRandomName(), getRandomID(),true);
        scaledrone = new Scaledrone(channelID,data);
        //initial connection
        scaledrone.connect(new ScaledroneListener());



    }

    public void sendMessage(View view) {
        String message = editText.getText().toString();
        if (message.length() > 0) {
            scaledrone.publish(roomName, message);
            editText.getText().clear();
        }

    }
    //chatbox
    @Override
    public void onOpen(Room room) {
        System.out.println("Connected to room");

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
        final model.Message message = new model.Message(receivedMessage.getData().asText(), 1 );
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
            super.onBackPressed();
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
        /*Intent intent = new Intent(ChatboxActivity.this, SetStudySessionActivity.class);
        startActivity(intent);*/
        scaledrone.publish(roomName, "Study Session");

    }

    private class ScaledroneListener implements Listener{

        @Override
        public void onOpen() {
            System.out.println("Scaledrone connection open");
            Room room=scaledrone.subscribe(roomName, ChatboxActivity.this,new SubscribeOptions(100));
            room.listenToHistoryEvents(new HistoryRoomListener() {
                @Override
                public void onHistoryMessage(Room room, Message receivedMessage) {
                    System.out.println("Received a message from the past client "+ receivedMessage.getClientID()+" "+ receivedMessage.getMember());
                    boolean belongsToCurrentUser = receivedMessage.getClientID().equals(scaledrone.getClientID());
                    final model.Message message = new model.Message(receivedMessage.getData().asText(), 1 );
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageAdapter.add(message);
                            messagesView.setSelection(messagesView.getCount() - 1);
                        }
                    });
                }
            });
            /*room.listenToObservableEvents(new ObservableRoomListener() {
                @Override
                public void onMembers(Room room, ArrayList<Member> members) {
                    // Emits an array of members that have joined the room. This event is only triggered once, right after the user has successfully connected to the observable room.
                    // Keep in mind that the session user will also be part of this array, so the minimum size of the array is 1
                    for (int i=0;i<members.size();i++){
                        System.out.println(members.get(i).toString());
                    }

                }

                @Override
                public void onMemberJoin(Room room, Member member) {
                    System.out.println("member joined "+member.toString());
                }

                @Override
                public void onMemberLeave(Room room, Member member) {
                    System.out.println("member disconnected "+member.toString());
                }
            });*/
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
    class MemberData {
        private String name;
        private int id;
        private boolean study;


        public MemberData(String name, int id, boolean study) {
            this.name = name;
            this.id = id;
            this.study=study;
        }

        public String getName() {
            return name;
        }


        public int getID() {
            return id;
        }

        @Override
        public String toString() {
            return "MemberData{" +
                    "name='" + name + '\'' +
                    ", id='" + id + '\'' +
                    ", study='" + study + '\'' +
                    '}';
        }
    }
    private String getRandomName() {
        String[] names = {  "Sam", "Mandy"};
        return names[(int) Math.floor(Math.random() * names.length)];
    }

    private int getRandomID() {
        int[] id = {  1, 2};
        return id[(int) Math.floor(Math.random() * id.length)];

    }


}
