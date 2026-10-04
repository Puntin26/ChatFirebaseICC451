package com.ianalvarez.chatfirebaseicc451.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ianalvarez.chatfirebaseicc451.R;
import com.ianalvarez.chatfirebaseicc451.adapter.UserAdapter;
import com.ianalvarez.chatfirebaseicc451.viewmodel.UsersViewModel;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private UsersViewModel usersViewModel;
    
    // UI elements del encabezado y buscador
    private TextView txtMyInitial, txtMyName, txtMyEmail, txtUserCount;
    private EditText etSearchUsers;
    private ImageButton btnClearSearch;

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

        // Inicializar vistas del buscador y perfil
        txtMyInitial = findViewById(R.id.txtMyInitial);
        txtMyName = findViewById(R.id.txtMyName);
        txtMyEmail = findViewById(R.id.txtMyEmail);
        txtUserCount = findViewById(R.id.txtUserCount);
        etSearchUsers = findViewById(R.id.etSearchUsers);
        btnClearSearch = findViewById(R.id.btnClearSearch);



        // Configuración del Toolbar y menú de cierre de sesión
        Toolbar toolbarUsers = findViewById(R.id.toolbarUsers);
        toolbarUsers.inflateMenu(R.menu.menu_users);
        toolbarUsers.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                confirmarCerrarSesion();
                return true;
            }
            return false;
        });

        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        usersViewModel.getUsersReadError().observe(this, failed -> {
            if (!Boolean.TRUE.equals(failed)) return;

            Toast.makeText(this, R.string.users_read_error, Toast.LENGTH_LONG).show();
            usersViewModel.clearUsersReadError();
        });

        usersViewModel.getProfileReadError().observe(this, failed -> {
            if (!Boolean.TRUE.equals(failed)) return;

            Toast.makeText(this, R.string.profile_read_error, Toast.LENGTH_LONG).show();
            usersViewModel.clearProfileReadError();
        });


        // Llenar datos del usuario logueado en el encabezado
        setupCurrentUserInfo();
        
        usersViewModel.getUsers().observe(this, userList -> {
            userAdapter = new UserAdapter(userList);
            rvUsers.setAdapter(userAdapter);
            txtUserCount.setText(userList.size() + " usuario(s)");
        });
        
        setupSearchLogic();

        // Solicitar permisos de notificación si es necesario
        askNotificationPermission();
    }

    private void setupCurrentUserInfo() {
        usersViewModel.getCurrentUserProfile().observe(this, user -> {
            if (user == null) {
                return;
            }

            String name = user.getName();
            String email = user.getEmail();

            if (name == null || name.trim().isEmpty()) {
                name = getString(R.string.profile_name_unavailable);
            }

            name = name.trim();

            txtMyName.setText(name);
            txtMyEmail.setText(email != null ? email : "");
            txtMyInitial.setText(name.substring(0, 1).toUpperCase());
        });
    }


    private void setupSearchLogic() {
        etSearchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btnClearSearch.setVisibility(View.VISIBLE);
                } else {
                    btnClearSearch.setVisibility(View.GONE);
                }
                
                if (userAdapter != null) {
                    userAdapter.filterList(s.toString());
                    // Opcional: actualizar el contador al filtrar, el adapter podría exponer su tamaño actual
                    txtUserCount.setText(userAdapter.getItemCount() + " usuario(s)");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearchUsers.setText("");
        });
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres salir de tu cuenta?")
                .setPositiveButton("Salir", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(this, LoginActivity.class);
                    // Borra el historial para que no pueda volver atrás con el botón de retroceso
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
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