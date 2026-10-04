package com.ianalvarez.chatfirebaseicc451.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.ianalvarez.chatfirebaseicc451.R;
import com.ianalvarez.chatfirebaseicc451.adapter.UserAdapter;
import com.ianalvarez.chatfirebaseicc451.viewmodel.UsersViewModel;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private UsersViewModel usersViewModel;

    // Lanzador para pedir permisos de notificaciones (Android 13+)
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permiso concedido
                } else {
                    // Permiso denegado
                }
            });

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

        // Configuración del Toolbar y menú de cierre de sesión
        Toolbar toolbarUsers = findViewById(R.id.toolbarUsers);
        toolbarUsers.inflateMenu(R.menu.menu_users);
        toolbarUsers.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });

        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        
        usersViewModel.getUsers().observe(this, userList -> {
            userAdapter = new UserAdapter(userList);
            rvUsers.setAdapter(userAdapter);
        });

        // Solicitar permisos de notificación si es necesario
        askNotificationPermission();
    }

    private void askNotificationPermission() {
        // En Android 13 (TIRAMISU) o superior, el permiso POST_NOTIFICATIONS es obligatorio.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                // Pide el permiso
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}