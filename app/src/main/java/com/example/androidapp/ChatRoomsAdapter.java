package com.example.androidapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.TimeUnit;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ChatRoomsAdapter extends RecyclerView.Adapter<ChatRoomsAdapter.MyHolder> {
    List<ChatRoom> roomslist;

    private Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;





    public  ChatRoomsAdapter(List<ChatRoom> roomslist, Context context)
    {
        this.context=context;
        this.roomslist=roomslist;

    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chatroom,viewGroup,false);
        MyHolder myHolder=new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {
        ChatRoom room=roomslist.get(position);
        myHolder.name.setText(room.getName());
        myHolder.description.setText(room.getDescription());
        if(room.isSecret()){
            myHolder.currentLayout.setBackgroundColor(Color.BLUE);
        }
    }

    @Override
    public int getItemCount() {
        return roomslist.size();
    }

    class  MyHolder extends RecyclerView.ViewHolder  {
        TextView name,description;
        CardView currentLayout;
        ChatRoom data;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            currentLayout=itemView.findViewById(R.id.chatroom_card_layout);
            name=itemView.findViewById(R.id.room_name);
            description=itemView.findViewById(R.id.room_description);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data =roomslist.get(getAdapterPosition());
                    if(data.isSecret()){
                        showEnterRoomDialog(data);
                    }else{
                        gotoChat(data);
                    }


                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    data =roomslist.get(getAdapterPosition());
                    if(data.getRoom_manager().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        showUDRoomDialog(data.getId(),data.getDescription(),data.getName(),data.isSecret(),data.getRoom_password());
                    }
                    return true;
                }
            });


        }


    }
    private void setInitChatRoomNotification(String current_chatroom_id){
        FirebaseDatabase database;
        DatabaseReference reference;
        database = FirebaseDatabase.getInstance();
        reference =  database.getReference("ChatRoomNotification");

            String id=reference.push().getKey();
            ChatRoomNotification crn = new ChatRoomNotification(id,
                    FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getEmail(),
                    current_chatroom_id,
                    true);

            reference.child(id).setValue(crn);
    }


    private void firstChatRoomComer(final String current_chatroom_id){

        FirebaseDatabase database;
        DatabaseReference reference;
        database = FirebaseDatabase.getInstance();
        reference =  database.getReference("ChatRoomNotification");

        Query query = reference.orderByChild("receiver")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //Toast.makeText(context, "dataSnapshot is exists", Toast.LENGTH_SHORT).show();

                }else{
                    //Toast.makeText(context, "dataSnapshot not  exists", Toast.LENGTH_SHORT).show();
                    setInitChatRoomNotification(current_chatroom_id);

                }
                int count = 0;

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    ChatRoomNotification crn = postSnapshot.getValue(ChatRoomNotification.class);

                    if(crn.getChatroom_id().equals(current_chatroom_id)){
                        //Toast.makeText(context, "in the db", Toast.LENGTH_SHORT).show();

                    }else{
                        //Toast.makeText(context, "no no no", Toast.LENGTH_SHORT).show();
                        count ++;
                        //setInitChatRoomNotification(current_chatroom_id);

                    }
                }

                if(count == dataSnapshot.getChildrenCount() && dataSnapshot.getChildrenCount() != 0){
                    //Toast.makeText(context, "create a init", Toast.LENGTH_SHORT).show();
                    setInitChatRoomNotification(current_chatroom_id);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void gotoChat(ChatRoom data){
        firstChatRoomComer(data.getId());

            Intent i=new Intent(context, Chat.class);

            i.putExtra("id",data.getId());
            i.putExtra("name",data.getName());
            //i.putExtra("description",data.getDescription());
            //i.putExtra("pass",data.getRoom_password());
            context.startActivity(i);


    }

    private void showUDRoomDialog(final String id,final String desc,final String name,final boolean secret,final String old_password) {
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("ChatRoom");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.ud_chatroom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.edit_update_room_name);
        final EditText editTextDescription = (EditText) dialogView.findViewById(R.id.edit_update_room_description);
        final Switch switchBoolSecret = (Switch)dialogView.findViewById(R.id.switch_update_room_secret);
        final EditText editTextPassword = (EditText) dialogView.findViewById(R.id.edit_update_room_password);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteChatRoom);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateChatRoom);

        editTextName.setText(name);
        editTextDescription.setText(desc);
        switchBoolSecret.setChecked(secret);
        if(secret){
            editTextPassword.setText(old_password);
        }else{
            editTextPassword.setVisibility(View.INVISIBLE);
        }

        dialogBuilder.setTitle("Update or Delete Chat Room");
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

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
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
                        UpdateChatRoom(id,name,desc,secret,pass);
                        b.dismiss();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Please check your input data.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cancel dialog
                deleteRoom(id);
                b.dismiss();
            }
        });

    }

    private void UpdateChatRoom(String id,String roomName, String roomDesc,boolean roomSecret,String roomPass)
    {
        String manager = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        ChatRoom room = new ChatRoom(id,roomName, roomDesc,roomSecret,roomPass,manager);
        databaseReference.child(id).setValue(room).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "Chat Room Updated", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void deleteRoom(String id) {
        databaseReference.child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Chat Room Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Boolean checkTitleAndContentIsEmpty(String n,String d){
        if(checkIsEmpty(n) && checkIsEmpty(d)){
            Toast.makeText(context, "Please enter Room Name and Description", Toast.LENGTH_SHORT).show();
            return true;
        }else if(checkIsEmpty(n)){
            Toast.makeText(context, "Please enter a Room Name", Toast.LENGTH_SHORT).show();
            return true;
        }else if(checkIsEmpty(d)){
            Toast.makeText(context, "Please enter a Description", Toast.LENGTH_SHORT).show();
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

    private void showEnterRoomDialog(final ChatRoom data) {
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("ChatRoom");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.chatroom_password_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextPassword = (EditText) dialogView.findViewById(R.id.edit_enter_room_password);
        final Button buttonEnter = (Button) dialogView.findViewById(R.id.buttonEnterChatRoom);
        final Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancelEnterChatRoom);


        dialogBuilder.setTitle("Enter Chat Room");
        final AlertDialog b = dialogBuilder.create();
        b.show();



        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = editTextPassword.getText().toString().trim();

                    //checking if the pass is correct
                    if (pass.equals(data.getRoom_password())) {
                        gotoChat(data);
                        b.dismiss();
                    }else{
                        Toast.makeText(context, "Room Password is not correct, Please check your Input.", Toast.LENGTH_SHORT).show();
                    }

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });

    }
}
