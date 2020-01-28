package com.example.androidapp;

public class ChatRoomNotification {
    private String receiver;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getChatroom_id() {
        return chatroom_id;
    }

    public void setChatroom_id(String chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    private String chatroom_id;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private boolean status;
    public ChatRoomNotification() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public ChatRoomNotification(String id,String receiver,String chatroom_id,boolean status){
        this.id = id;
        this.receiver = receiver;
        this.chatroom_id = chatroom_id;
        this.status = status;
    }

}
