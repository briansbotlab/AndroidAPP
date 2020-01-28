package com.example.androidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostCenter extends AppCompatActivity {

    //  Firebase Database
    FirebaseDatabase database;
    DatabaseReference reference;

    //a list to store all the posts from firebase database
    List<Post> posts;

    Button add_post;
    ProgressBar loadingProgressBar;

    //list view
    ListView listViewPosts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_center);

        add_post = (Button)findViewById(R.id.post_add);
        loadingProgressBar = findViewById(R.id.loading_post);

        database = FirebaseDatabase.getInstance();
        reference =  database.getReference("Posts");

        //list to store posts
        posts = new ArrayList<>();

        //list view
        listViewPosts = (ListView) findViewById(R.id.listViewPosts);



        listViewPosts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Post post = posts.get(i);
                if(post.getPost_by().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    showUpdateDeleteDialog(post.getId(), post.getTitle(),post.getContent());
                }
                return true;
            }
        });
        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPostDialog();
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
        Query query = reference.orderByChild("post_date");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous post list
                posts.clear();
                loadingProgressBar.setVisibility(View.INVISIBLE);
                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting post
                    Post post = postSnapshot.getValue(Post.class);
                    //adding post to the list
                    posts.add(post);
                }
                Collections.reverse(posts);

                //creating adapter
                PostList postAdapter = new PostList(PostCenter.this, posts);
                //attaching adapter to the listview


                listViewPosts.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed()會自動呼叫finish()方法,關閉
        //super.onBackPressed();
        gotoMenuActivity();
    }


    private  void gotoMenuActivity(){
        //建立一個意圖,引數為（當前的Activity類物件，需要開的的Activity類）
        Intent intent = new Intent(PostCenter.this, MenuActivity.class);
        intent.putExtra("key", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        //啟動意圖
        startActivity(intent);
    }

    private void showAddPostDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_post_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextTitle = (EditText) dialogView.findViewById(R.id.edit_add_post_title);
        final EditText editTextContent = (EditText) dialogView.findViewById(R.id.edit_add_post_content);

        final Button buttonAdd = (Button) dialogView.findViewById(R.id.buttonAddPost);
        final Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancel);

        dialogBuilder.setTitle("Add Post");
        final AlertDialog b = dialogBuilder.create();
        b.show();


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString().trim();
                String content = editTextContent.getText().toString().trim();


                try {
                    //checking if the value is provided
                    if (!checkTitleAndContentIsEmpty(title, content)) {
                        //add chat room
                        addPost(title, content);
                        b.dismiss();
                    }
                } catch (Exception e) {
                    Toast.makeText(PostCenter.this, "Please check your input data.", Toast.LENGTH_SHORT).show();
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
    private void addPost(String title, String content) {

        String id=reference.push().getKey();
        String post_by = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Post post = new Post(id,title, content,post_by);
        reference.child(id).setValue(post).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(PostCenter.this, "Post Added", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private boolean updatePost(String id, String title, String content) {
        //getting the specified post reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Posts").child(id);
        String post_by = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        //updating post
        Post post = new Post(id, title, content, post_by);
        dR.setValue(post);
        Toast.makeText(getApplicationContext(), "Post Updated", Toast.LENGTH_SHORT).show();
        return true;
    }
    private boolean deletePost(String id) {
        //getting the specified post reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Posts").child(id);
        //removing post
        dR.removeValue();
        Toast.makeText(getApplicationContext(), "Post Deleted", Toast.LENGTH_SHORT).show();
        return true;
    }


    private void showUpdateDeleteDialog(final String postId, String postTitle, String postContent) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.up_post_dialog, null);
        dialogBuilder.setView(dialogView);


        final EditText editTextContent = (EditText) dialogView.findViewById(R.id.editTextContent);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdatePost);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeletePost);
        editTextContent.setText(postContent);
        dialogBuilder.setTitle(postTitle);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        final String title = postTitle;
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editTextContent.getText().toString().trim();
                try {
                    //checking if the value is provided
                    if (!checkTitleAndContentIsEmpty(title, content)) {


                        updatePost(postId, title,content);
                        b.dismiss();
                    }
                }catch(Exception e){
                    Toast.makeText(PostCenter.this,"Please check your input data.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePost(postId);
                b.dismiss();
            }
        });
    }


    private Boolean checkTitleAndContentIsEmpty(String t,String c){
        if(checkIsEmpty(t) && checkIsEmpty(c)){
            Toast.makeText(this, "Please enter Title and Content", Toast.LENGTH_SHORT).show();
            return true;
        }else if(checkIsEmpty(t)){
            Toast.makeText(this, "Please enter a Title", Toast.LENGTH_SHORT).show();
            return true;
        }else if(checkIsEmpty(c)){
            Toast.makeText(this, "Please enter a Content", Toast.LENGTH_SHORT).show();
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
