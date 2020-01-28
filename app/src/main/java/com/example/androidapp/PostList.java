package com.example.androidapp;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PostList extends ArrayAdapter<Post> {
    private Activity context;
    List<Post> posts;

    public PostList(Activity context, List<Post> posts) {
        super(context, R.layout.post, posts);
        this.context = context;
        this.posts = posts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.post, null, true);

        TextView textViewTitle = (TextView) listViewItem.findViewById(R.id.textViewTitle);
        TextView textViewContent = (TextView) listViewItem.findViewById(R.id.textViewContent);
        TextView textViewPostBy = (TextView) listViewItem.findViewById(R.id.textViewPostBy);
        TextView textViewPostDate = (TextView) listViewItem.findViewById(R.id.textViewPostDate);

        Post post = posts.get(position);
        textViewTitle.setText(post.getTitle());
        textViewContent.setText(post.getContent());
        textViewPostBy.setText(post.getPost_by());
        // Format the date before showing it
        textViewPostDate.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                post.getPost_date()));

        return listViewItem;
    }
}
