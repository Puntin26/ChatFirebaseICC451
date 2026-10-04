package com.ianalvarez.chatfirebaseicc451.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
    private String currentChatId = "general_chat"; // Temporal, luego podemos hacerlo dinámico entre usuarios

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
        messageList = new ArrayList<>();
        
        String currentUserId = chatViewModel.getCurrentUserId();
        messageAdapter = new MessageAdapter(messageList, currentUserId);
        
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

        // Configuración para evitar mensajes vacíos (Requisito de la rúbrica)
        btnSendMessage.setOnClickListener(v -> {
            String message = etMessageText.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show();
            } else {
                sendMessage(message);
            }
        });
        
        // El botón de adjuntar imagen lo preparamos para el Sábado
        btnAttachImage.setOnClickListener(v -> {
            Toast.makeText(this, "Función de adjuntar imagen próximamente", Toast.LENGTH_SHORT).show();
        });
    }

    private void sendMessage(String text) {
        chatViewModel.sendMessage(currentChatId, text);
        etMessageText.setText("");
    }
}
