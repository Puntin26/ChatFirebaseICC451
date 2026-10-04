package com.ianalvarez.chatfirebaseicc451.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ianalvarez.chatfirebaseicc451.model.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;

    public ChatRepository() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public String getCurrentUserId() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
        return null;
    }

    public String getCurrentUserName() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getDisplayName() != null ? auth.getCurrentUser().getDisplayName() : auth.getCurrentUser().getEmail();
        }
        return "Anónimo";
    }

    public void sendMessage(String chatId, Message message) {
        // En Firestore, guardaremos los mensajes dentro de una subcolección "messages" de la colección "chats"
        String messageId = firestore.collection("chats").document(chatId).collection("messages").document().getId();
        message.setMessageId(messageId);

        firestore.collection("chats").document(chatId).collection("messages").document(messageId)
                .set(message)
                .addOnSuccessListener(aVoid -> Log.d("ChatRepository", "Mensaje enviado exitosamente"))
                .addOnFailureListener(e -> Log.e("ChatRepository", "Error enviando el mensaje", e));
    }

    public LiveData<List<Message>> getMessages(String chatId) {
        MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>();

        // Escuchar cambios en tiempo real ordenados por timestamp
        firestore.collection("chats").document(chatId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("ChatRepository", "Listen failed.", error);
                        return;
                    }

                    List<Message> messages = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Message message = doc.toObject(Message.class);
                            messages.add(message);
                        }
                    }
                    messagesLiveData.setValue(messages);
                });

        return messagesLiveData;
    }
}