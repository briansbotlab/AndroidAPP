package com.example.androidapp;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.List;

public class MessageList extends ArrayAdapter<Message> {
    private Activity context;
    List<Message> messages;

    public MessageList(Activity context, List<Message> messages) {
        super(context, R.layout.message, messages);
        this.context = context;
        this.messages = messages;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.message, null, true);

        TextView messageText = (TextView)listViewItem.findViewById(R.id.message_text);
        TextView messageUser = (TextView)listViewItem.findViewById(R.id.message_from);
        TextView messageTime = (TextView)listViewItem.findViewById(R.id.message_time);


        Message m = messages.get(position);
        messageText.setText(m.getMessage());
        messageUser.setText(m.getFromMail());
        // Format the date before showing it
        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                m.getSentDate()));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(m.getFromMail().compareTo(user.getEmail()) == 0) {
            messageUser.setText("Me");

            RelativeLayout currentLayout  = (RelativeLayout) listViewItem.findViewById(R.id.message_layout);
            currentLayout .setBackgroundColor(Color.YELLOW);
        }


        return listViewItem;
    }


}
