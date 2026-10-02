package com.ianalvarez.chatfirebaseicc451.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.ianalvarez.chatfirebaseicc451.R;
import com.ianalvarez.chatfirebaseicc451.viewmodel.AuthViewModel;


import com.ianalvarez.chatfirebaseicc451.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtRegisterName;
    private EditText txtRegisterEmail;
    private EditText txtRegisterPassword;
    private EditText txtRegisterConfirmPassword;
    private Button btnRegister;
    private TextView txtGoToLogin;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtRegisterName = findViewById(R.id.txtRegisterName);
        txtRegisterEmail = findViewById(R.id.txtRegisterEmail);
        txtRegisterPassword = findViewById(R.id.txtRegisterPassword);
        txtRegisterConfirmPassword = findViewById(R.id.txtRegisterConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtGoToLogin = findViewById(R.id.txtGoToLogin);

        txtGoToLogin.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnRegister.setOnClickListener(view -> {
            authViewModel.register(
                    txtRegisterName.getText().toString(),
                    txtRegisterEmail.getText().toString(),
                    txtRegisterPassword.getText().toString(),
                    txtRegisterConfirmPassword.getText().toString()
            );
        });

        authViewModel.getRegisterResult().observe(this, result -> {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_LONG).show();

            if (result.isSuccess()) {
                Intent intent = new Intent(this, UsersActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}