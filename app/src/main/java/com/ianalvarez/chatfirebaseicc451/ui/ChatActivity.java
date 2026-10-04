package com.ianalvarez.chatfirebaseicc451.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ianalvarez.chatfirebaseicc451.R;
import com.ianalvarez.chatfirebaseicc451.adapter.MessageAdapter;
import com.ianalvarez.chatfirebaseicc451.model.Message;
import com.ianalvarez.chatfirebaseicc451.viewmodel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessageText;
    private ImageButton btnSendMessage;
    private ImageButton btnAttachImage;

    private ChatViewModel chatViewModel;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private String currentChatId;
    private String receiverId;
    private String receiverName;

    // Para seleccionar imágenes
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvMessages = findViewById(R.id.rvMessages);
        etMessageText = findViewById(R.id.etMessageText);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        btnAttachImage = findViewById(R.id.btnAttachImage);

        // Configuración de MVVM y RecyclerView
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        chatViewModel.getSending().observe(this, sending -> {
            boolean enabled = !Boolean.TRUE.equals(sending);

            btnSendMessage.setEnabled(enabled);
            btnAttachImage.setEnabled(enabled);
        });

        chatViewModel.getSentText().observe(this, sentText -> {
            if (sentText == null) {
                return;
            }

            // Solo borra si el usuario no cambió lo escrito durante el envío.
            String currentText = etMessageText.getText().toString().trim();

            if (currentText.equals(sentText)) {
                etMessageText.setText("");
            }

            chatViewModel.clearSendResult();
        });

        chatViewModel.getSendError().observe(this, error -> {
            if (error == null) {
                return;
            }

            Toast.makeText(
                    this,
                    R.string.message_send_error,
                    Toast.LENGTH_LONG
            ).show();

            chatViewModel.clearSendResult();
        });

        chatViewModel.getMessagesReadError().observe(this, failed -> {
            if (!Boolean.TRUE.equals(failed)) {
                return;
            }

            Toast.makeText(this, R.string.messages_read_error, Toast.LENGTH_LONG).show();

            chatViewModel.clearMessagesReadError();
        });

        messageList = new ArrayList<>();
        
        // Recibir datos de la otra persona desde el Intent
        Intent intent = getIntent();
        receiverId = intent.getStringExtra("receiverId");
        receiverName = intent.getStringExtra("receiverName");
        
        // Configurar la barra superior con nombre y botón de retroceso
        Toolbar toolbarChat = findViewById(R.id.toolbarChat);
        toolbarChat.setTitle(receiverName != null ? receiverName : "Chat");
        toolbarChat.setNavigationOnClickListener(v -> finish()); // Retroceder al darle clic

        String currentUserId = chatViewModel.getCurrentUserId();
        
        // Generar un ID de chat único combinando los dos IDs y ordenándolos alfabéticamente
        // para que siempre sea el mismo chat sin importar quién escribe primero
        if (currentUserId.compareTo(receiverId) < 0) {
            currentChatId = currentUserId + "_" + receiverId;
        } else {
            currentChatId = receiverId + "_" + currentUserId;
        }

        messageAdapter = new MessageAdapter(messageList, currentUserId);

        // Observador para saber cuándo se está subiendo una imagen y bloquear el botón mientras tanto

        chatViewModel.getImageError().observe(this, error -> {
            if (error == null) {
                return;
            }

            Toast.makeText(this, R.string.image_upload_error, Toast.LENGTH_LONG).show();

            chatViewModel.clearImageError();
        });

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // Enviamos la URI de la imagen seleccionada para subirla a Storage
                        chatViewModel.sendImageMessage(currentChatId, uri);
                    }
                }
        );
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Para que los mensajes salgan desde abajo
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(messageAdapter);

        // Observar los mensajes en tiempo real
        chatViewModel.getMessages(currentChatId).observe(this, messages -> {
            messageList.clear();
            messageList.addAll(messages);
            messageAdapter.notifyDataSetChanged();
            if (messageList.size() > 0) {
                // Hacer scroll automático al último mensaje
                rvMessages.smoothScrollToPosition(messageList.size() - 1);
            }
        });

        // Configuración para evitar mensajes vacíos
        btnSendMessage.setOnClickListener(v -> {
            String message = etMessageText.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show();
            } else {
                sendMessage(message);
            }
        });
        
        // El botón de adjuntar imagen lanza el selector de imágenes
        btnAttachImage.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
        });
    }

    private void sendMessage(String text) {
        chatViewModel.sendMessage(currentChatId, text);
    }
}
