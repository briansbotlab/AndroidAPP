package com.example.androidapp;


public class ChatRoom {
    private String id;
    private String name;
    private String description;
    private boolean secret;
    private String room_password;
    private String room_manager;
    public ChatRoom(){
        // Default constructor required for calls to DataSnapshot.getValue(ChatRoom.class)
    }

    public ChatRoom(String id,String name,String description,boolean secret,String room_password,String room_manager){
        this.id = id;
        this.name = name;
        this.description = description;
        this.secret = secret;
        this.room_password = room_password;
        this.room_manager = room_manager;
    }

    public String getRoom_manager() {
        return room_manager;
    }

    public void setRoom_manager(String room_manager) {
        this.room_manager = room_manager;
    }

    public boolean isSecret() {
        return secret;
    }

    public void setSecret(boolean secret) {
        this.secret = secret;
    }
/*
    public void setRoom_password(String room_password) {
        this.room_password = PasswordSecurity.encryptAndEncode(room_password);
    }

    public String getRoom_password(){
        try {
            return this.room_password = PasswordSecurity.decodeAndDecrypt(this.getRoom_password());
        }catch (Exception e){
            System.out.println(e);
            return "";
        }

    }


 */

    public String getRoom_password() {
        return room_password;
    }

    public void setRoom_password(String room_password) {
        this.room_password = room_password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
