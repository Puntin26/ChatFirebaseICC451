package com.ianalvarez.chatfirebaseicc451.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ianalvarez.chatfirebaseicc451.R;
import com.ianalvarez.chatfirebaseicc451.adapter.UserAdapter;
import com.ianalvarez.chatfirebaseicc451.viewmodel.UsersViewModel;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private UsersViewModel usersViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_users);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvUsers = findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        
        usersViewModel.getUsers().observe(this, userList -> {
            userAdapter = new UserAdapter(userList);
            rvUsers.setAdapter(userAdapter);
        });
    }
}