package com.example.androidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class RoomCenter extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ProgressBar loadingProgressBar;
    List<ChatRoom> chatroomlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_center);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("ChatRoom");

        chatroomlist = new ArrayList<>();

        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(RoomCenter.this);
        recyclerView.setLayoutManager(layoutManager);

        final ChatRoomsAdapter roomAdapter=new ChatRoomsAdapter(chatroomlist,this);
        recyclerView.setAdapter(roomAdapter);
        FloatingActionButton fab = findViewById(R.id.room_fab);

        loadingProgressBar = findViewById(R.id.loading_chatroom);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddRoomDialog();
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        final ChatRoomsAdapter roomAdapter = new ChatRoomsAdapter(chatroomlist,RoomCenter.this);

        //attaching value event listener
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chatroomlist.clear();

                loadingProgressBar.setVisibility(View.INVISIBLE);
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    ChatRoom room=dataSnapshot1.getValue(ChatRoom.class);
                    chatroomlist.add(room);

                }
                roomAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(roomAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        roomAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(roomAdapter);
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed()會自動呼叫finish()方法,關閉
        //super.onBackPressed();
        gotoMenuActivity(FirebaseAuth.getInstance().getCurrentUser());
    }

    private  void gotoMenuActivity(FirebaseUser currentUser){
        //建立一個意圖,引數為（當前的Activity類物件，需要開的的Activity類）
        Intent intent = new Intent(RoomCenter.this, MenuActivity.class);

        String value = "NO DATA";

        if (currentUser != null) {
            value = currentUser.getEmail();
        }

        intent.putExtra("key", value);
        //啟動意圖
        startActivity(intent);

        RoomCenter.this.finish();
    }

    private void showAddRoomDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_chatroom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.edit_add_room_name);
        final EditText editTextDescription = (EditText) dialogView.findViewById(R.id.edit_add_room_description);
        final Switch switchBoolSecret = (Switch)dialogView.findViewById(R.id.switch_add_room_secret);
        final EditText editTextPassword = (EditText) dialogView.findViewById(R.id.edit_add_room_password);
        final Button buttonAdd = (Button) dialogView.findViewById(R.id.buttonAddChatRoom);
        final Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancel);

        dialogBuilder.setTitle("Add Chat Room");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        switchBoolSecret.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) //Line A
            {
                if(isChecked){
                    editTextPassword.setVisibility(View.VISIBLE);
                }else{
                    editTextPassword.setVisibility(View.INVISIBLE);
                    editTextPassword.setText("");
                }
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String desc = editTextDescription.getText().toString().trim();
                boolean secret =  switchBoolSecret.isChecked();
                String pass = editTextPassword.getText().toString().trim();

                try {
                    //checking if the value is provided
                    if (!checkTitleAndContentIsEmpty(name, desc)) {
                       //add chat room
                        AddChatRoom(name,desc,secret,pass);
                        b.dismiss();
                    }
                } catch (Exception e) {
                    Toast.makeText(RoomCenter.this, "Please check your input data.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //cancel dialog
                b.dismiss();
            }
        });

    }

    private void AddChatRoom(String roomName, String roomDesc,boolean roomSecret,String roomPass)
    {

        String id=databaseReference.push().getKey();
        String manager = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        ChatRoom room = new ChatRoom(id,roomName, roomDesc,roomSecret,roomPass,manager);
        databaseReference.child(id).setValue(room).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(RoomCenter.this, "Chat Room Added", Toast.LENGTH_SHORT).show();
                    }
                });

    }



    private Boolean checkTitleAndContentIsEmpty(String n,String d){
        if(checkIsEmpty(n) && checkIsEmpty(d)){
            Toast.makeText(this, "Please enter Room Name and Description", Toast.LENGTH_SHORT).show();
            return true;
        }else if(checkIsEmpty(n)){
            Toast.makeText(this, "Please enter a Room Name", Toast.LENGTH_SHORT).show();
            return true;
        }else if(checkIsEmpty(d)){
            Toast.makeText(this, "Please enter a Description", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            return false;
        }
    }

    private Boolean checkIsEmpty(String s){
        if(TextUtils.isEmpty(s)){
            return true;
        }else{
            return false;
        }
    }
}
