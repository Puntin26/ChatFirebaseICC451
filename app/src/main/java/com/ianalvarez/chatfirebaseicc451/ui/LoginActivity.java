package com.ianalvarez.chatfirebaseicc451.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.ianalvarez.chatfirebaseicc451.R;
import com.ianalvarez.chatfirebaseicc451.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText txtLoginEmail;
    private EditText txtLoginPassword;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        txtLoginEmail = findViewById(R.id.txtLoginEmail);
        txtLoginPassword = findViewById(R.id.txtLoginPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        View layoutGoToRegister = findViewById(R.id.layoutGoToRegister);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnLogin.setOnClickListener(view -> {
            authViewModel.login(txtLoginEmail.getText().toString(), txtLoginPassword.getText()
                                                                     .toString());
        });

        authViewModel.getLoginResult().observe(this, result -> {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();

            if (result.isSuccess()) {
                startActivity(new Intent(this, UsersActivity.class));
                finish();
            }
        });

        layoutGoToRegister.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

    }
}