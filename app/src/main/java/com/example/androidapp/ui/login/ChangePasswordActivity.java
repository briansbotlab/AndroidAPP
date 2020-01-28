package com.example.androidapp.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.androidapp.MenuActivity;
import com.example.androidapp.PostCenter;
import com.example.androidapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private LoginViewModel loginViewModel;
    EditText passwordEditText;
    Button changePasswordButton;
    ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        passwordEditText = findViewById(R.id.password_c);
        changePasswordButton = findViewById(R.id.change_password);
        loadingProgressBar = findViewById(R.id.loading_c);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                changePasswordButton.setEnabled(loginFormState.isDataValid());

                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });



        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(user.getEmail(),
                        passwordEditText.getText().toString());
            }
        };

        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(user.getEmail(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                String password = passwordEditText.getText().toString();
                updatePassword(password);
                loadingProgressBar.setVisibility(View.INVISIBLE);
            }
        });



    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed()會自動呼叫finish()方法,關閉
        //super.onBackPressed();
        gotoMenuActivity();
    }

    private  void gotoMenuActivity(){
        //建立一個意圖,引數為（當前的Activity類物件，需要開的的Activity類）
        Intent intent = new Intent(ChangePasswordActivity.this, MenuActivity.class);
        intent.putExtra("key", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        //啟動意圖
        startActivity(intent);
    }

    private void updatePassword(String password) {

        final FirebaseUser user = mAuth.getCurrentUser();
        final String newPwd = password;

        user.updatePassword(newPwd)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this,
                                    "Password has been updated",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "updatePassword:failure Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
