package com.example.androidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {
    ListView listOfMessages;
    //  Firebase Database
    FirebaseDatabase database;
    DatabaseReference reference;

    //a list to store all the posts from firebase database
    List<Message> messages;

    FloatingActionButton fab;
    EditText input;
    TextView toolbar_title;
    Button toolbar_button;
    Drawable d_mode_off,d_mode_on;

    //Chat Room Information
    String ChatRoomId;
    String ChatRoomName;

    String chatRoomNotificationId;


    List<ChatRoomNotification> crnlist;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar_title = (TextView) findViewById(R.id.chat_toolbar_text);
        toolbar_button = (Button) findViewById(R.id.chat_toolbar_button);

        database = FirebaseDatabase.getInstance();
        reference =  database.getReference("Messages");

        //list to store posts
        messages = new ArrayList<>();
        listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        input = (EditText)findViewById(R.id.input);

        // 得到Intent
        intent = getIntent();
        // get chat room information
        ChatRoomId = intent.getStringExtra("id");
        ChatRoomName = intent.getStringExtra("name");

        //init  crn list
        crnlist = new ArrayList<>();
        //put data to crn list
        setCRNlistInit();


        toolbar_title.setText(ChatRoomName);


        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setEnabled(false);
        fab.hide();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                Message m = new Message(ChatRoomId,
                        input.getText().toString(),
                        FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getEmail());

                FirebaseDatabase.getInstance()
                        .getReference("Messages")
                        .push()
                        .setValue(m);

                // Clear the input
                input.setText("");

            }
        });


        toolbar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int icon_off =android.R.drawable.ic_lock_silent_mode_off;
                int icon_on =android.R.drawable.ic_lock_silent_mode;
                d_mode_off = getResources().getDrawable(icon_off);
                d_mode_on = getResources().getDrawable(icon_on);
                if(toolbar_button.getBackground().getConstantState().equals(d_mode_off.getConstantState())){
                    updateChatRoomNotification(chatRoomNotificationId,ChatRoomId,true,"Silent mode on");
                    toolbar_button.setBackground(d_mode_on);
                }else{
                    updateChatRoomNotification(chatRoomNotificationId,ChatRoomId,false,"Silent mode off");
                    toolbar_button.setBackground(d_mode_off);
                }


            }
        });

        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    fab.setEnabled(true);
                    fab.show();
                }else{
                    fab.setEnabled(false);
                    fab.hide();
                }

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    @Override
    protected void onStart() {
        super.onStart();
        //attaching value event listener
        Query query = reference.orderByChild("chatRoomId").equalTo(ChatRoomId);
        //final int height_of_listOfMessages = listOfMessages.getHeight();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous message list
                messages.clear();
                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Message message = postSnapshot.getValue(Message.class);
                    //adding artist to the list
                    messages.add(message);
                }

                if(messages.size() > 0){
                    Message currentMessage = messages.get(messages.size()-1);
                    if(!currentMessage.getFromMail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        String context = currentMessage.getFromMail() + " : " + currentMessage.getMessage();
                        notifyUser(ChatRoomId,ChatRoomName,context);
                    }
                }



                //creating adapter
                MessageList messageAdapter = new MessageList(Chat.this, messages);
                //attaching adapter to the listview

                listOfMessages.setAdapter(messageAdapter);

                //Toast.makeText(Chat.this,Integer.toString(calculateHeight(listOfMessages)), Toast.LENGTH_SHORT).show();
                //Toast.makeText(Chat.this,Integer.toString(height_of_listOfMessages), Toast.LENGTH_SHORT).show();

                if (calculateHeight(listOfMessages) > listOfMessages.getHeight()) {
                    listOfMessages.setStackFromBottom(true);
                } else {
                    listOfMessages.setStackFromBottom(false);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void notifyUser(String ChatRoomId,String title,String content){
        for(ChatRoomNotification crn : crnlist) {
            if(crn.getChatroom_id().equals(ChatRoomId) && !crn.isStatus()){
                showNotification(title,content);
            }
        }
    }

    private void showNotification(String title,String content){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(getApplicationContext(),Chat.class);

                //.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //notificationIntent.putExtra("id",ChatRoomId);
        //notificationIntent.putExtra("name",ChatRoomName);


        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =

                (NotificationCompat.Builder) new NotificationCompat.Builder(this)

                        .setSmallIcon( android.R.drawable.stat_notify_chat)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentIntent(contentIntent)
                        .setFullScreenIntent(contentIntent, true)
                        .setAutoCancel(true);



        mNotificationManager.notify(0, mBuilder.build());
    }


    private void setCRNlistInit(){
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference =  database.getReference("ChatRoomNotification");

        Query query = reference.orderByChild("receiver")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        //clearing the previous message list

        query.addValueEventListener(new ValueEventListener() { @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            crnlist.clear();
            //iterating through all the nodes
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                //getting artist
                ChatRoomNotification crn = postSnapshot.getValue(ChatRoomNotification.class);
                //adding artist to the list
                crnlist.add(crn);
                //Toast.makeText(Chat.this,crn.getId(), Toast.LENGTH_SHORT).show();
            }
            chatRoomNotificationId = getCurrentCRNID(ChatRoomId);

        }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private String getCurrentCRNID(String ChatRoomId){
        //Toast.makeText(Chat.this,"aaa:" + crnlist.toString(), Toast.LENGTH_SHORT).show();
        String tmp = null;
        boolean currentChatRoomNotificationSatus = true;
        for(ChatRoomNotification crn : crnlist) {
            //Toast.makeText(Chat.this,"aaa:" + crn.getId(), Toast.LENGTH_SHORT).show();
            if(crn.getChatroom_id().equals(ChatRoomId)){
                //Toast.makeText(Chat.this,"find:" + crn.getId(), Toast.LENGTH_SHORT).show();
                tmp = crn.getId();
                currentChatRoomNotificationSatus = crn.isStatus();
            }
        }

        int icon_off =android.R.drawable.ic_lock_silent_mode_off;
        int icon_on =android.R.drawable.ic_lock_silent_mode;
        d_mode_off = getResources().getDrawable(icon_off);
        d_mode_on = getResources().getDrawable(icon_on);
        // loading init data...
        if(currentChatRoomNotificationSatus){
            toolbar_button.setBackground(d_mode_on);
        }else{
            toolbar_button.setBackground(d_mode_off);
        }
        return tmp;
    }


    @Override
    public void onBackPressed() {
        // super.onBackPressed()會自動呼叫finish()方法,關閉
        //super.onBackPressed();
        gotoChatRoomCenter();
    }

    private  void gotoChatRoomCenter(){
        //建立一個意圖,引數為（當前的Activity類物件，需要開的的Activity類）
        Intent intent = new Intent(Chat.this, RoomCenter.class);
        //啟動意圖
        startActivity(intent);
        Chat.this.finish();
    }

    private int calculateHeight(ListView list) {

        int height = 0;

        for (int i = 0; i < list.getCount(); i++) {
            View childView = list.getAdapter().getView(i, null, list);
            childView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            height+= childView.getMeasuredHeight();
        }

        //dividers height
        height += list.getDividerHeight() * list.getCount();

        return height;
    }

    private void updateChatRoomNotification(String chatRoomNotificationId,String current_chatroom_id,boolean status,final String completetext){
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        ChatRoomNotification crn = new ChatRoomNotification(chatRoomNotificationId,user,current_chatroom_id,status);
        database = FirebaseDatabase.getInstance();
        reference =  database.getReference("ChatRoomNotification");

        reference.child(chatRoomNotificationId).setValue(crn).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(Chat.this, completetext, Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
