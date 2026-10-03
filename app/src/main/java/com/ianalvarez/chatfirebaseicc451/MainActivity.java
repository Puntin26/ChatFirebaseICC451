package com.ianalvarez.chatfirebaseicc451;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.ianalvarez.chatfirebaseicc451.ui.LoginActivity;
import com.ianalvarez.chatfirebaseicc451.ui.UsersActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, UsersActivity.class));

        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();
    }
}