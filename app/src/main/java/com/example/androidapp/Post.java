package com.example.androidapp;


import java.util.Date;

public class Post {
    private String id;
    private String post_by;
    private String title;
    private String content;
    private long post_date;

    public String getPost_by() {
        return post_by;
    }

    public void setPost_by(String post_by) {
        this.post_by = post_by;
    }

    public long getPost_date() {
        return post_date;
    }

    public void setPost_date(long post_date) {
        this.post_date = post_date;
    }



    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }
    public Post(String id,String title, String content,String post_by) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.post_date = new Date().getTime();
        this.post_by = post_by;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
