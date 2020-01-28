package com.example.androidapp;

import java.util.Date;

public class Message {
    private String FromMail;
    private String ToMail;
    private String Message;
    private long  SentDate;
    private String ChatRoomId;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }
    public Message(String ChatRoomId,String Message, String FromMail) {
        this.ChatRoomId = ChatRoomId;
        this.Message = Message;
        this.FromMail = FromMail;

        // Initialize to current time
        SentDate = new Date().getTime();
    }
    public String getFromMail() {
        return FromMail;
    }

    public String getChatRoomId() {
        return ChatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        ChatRoomId = chatRoomId;
    }

    public void setFromMail(String fromMail) {
        FromMail = fromMail;
    }

    public String getToMail() {
        return ToMail;
    }

    public void setToMail(String toMail) {
        ToMail = toMail;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public long  getSentDate() {
        return SentDate;
    }

    public void setSentDate(long  sentDate) {
        SentDate = sentDate;
    }

}
