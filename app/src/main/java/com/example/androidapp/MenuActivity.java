package com.example.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidapp.ui.login.ChangePasswordActivity;
import com.example.androidapp.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;


public class MenuActivity extends AppCompatActivity {

    Button btn_logout,btn_chat,btn_changepassword,btn_post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        final TextView hi = (TextView) findViewById(R.id.hi);
        TextView tv_key = (TextView) findViewById(R.id.tv_key);
        btn_logout = (Button)findViewById(R.id.buttonLogout);
        btn_post = (Button)findViewById(R.id.buttonPost);
        btn_chat = (Button)findViewById(R.id.buttonChat);
        btn_changepassword = (Button)findViewById(R.id.buttonChangePassword);
        // 得到Intent
        Intent intent = getIntent();
        // 通過key得到值 值為字串型別
        String value = intent.getStringExtra("key");
        // 在TextView上設定 值
        tv_key.setText(value);


        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                gotoLogInActivity();
            }
        });
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPostCenter();
            }
        });
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChatRoomCenter();
            }
        });
        btn_changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChangePasswordActivity();
            }
        });
    }


    private  void gotoLogInActivity(){
        //建立一個意圖,引數為（當前的Activity類物件，需要開的的Activity類）
        Intent intent = new Intent(MenuActivity.this,LoginActivity.class);
        //啟動意圖
        startActivity(intent);
    }

    private  void gotoPostCenter(){
        //建立一個意圖,引數為（當前的Activity類物件，需要開的的Activity類）
        Intent intent = new Intent(MenuActivity.this, PostCenter.class);
        //啟動意圖
        startActivity(intent);
        MenuActivity.this.finish();
    }

    private  void gotoChatRoomCenter(){
        //建立一個意圖,引數為（當前的Activity類物件，需要開的的Activity類）
        Intent intent = new Intent(MenuActivity.this, RoomCenter.class);
        //啟動意圖
        startActivity(intent);
        MenuActivity.this.finish();
    }

    private  void gotoChangePasswordActivity(){
        //建立一個意圖,引數為（當前的Activity類物件，需要開的的Activity類）
        Intent intent = new Intent(MenuActivity.this, ChangePasswordActivity.class);
        //啟動意圖
        startActivity(intent);
        MenuActivity.this.finish();
    }
}
