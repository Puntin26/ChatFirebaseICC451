package com.ianalvarez.chatfirebaseicc451.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ianalvarez.chatfirebaseicc451.model.Message;
import com.ianalvarez.chatfirebaseicc451.repository.ChatRepository;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;
    private LiveData<List<Message>> messagesLiveData;

    public ChatViewModel() {
        chatRepository = new ChatRepository();
    }

    public String getCurrentUserId() {
        return chatRepository.getCurrentUserId();
    }

    public void sendMessage(String chatId, String text) {
        String senderId = chatRepository.getCurrentUserId();
        String senderName = chatRepository.getCurrentUserName();
        long timestamp = System.currentTimeMillis();

        Message message = new Message("", senderId, senderName, text, timestamp, null);
        chatRepository.sendMessage(chatId, message);
    }

    public void sendImageMessage(String chatId, Uri imageUri, MutableLiveData<Boolean> uploadState) {
        // Indicamos que empezó a subir
        uploadState.setValue(true);
        
        chatRepository.uploadImage(imageUri, chatId, new ChatRepository.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                // Cuando se sube la imagen, creamos el mensaje de texto vacío (o con la foto)
                String senderId = chatRepository.getCurrentUserId();
                String senderName = chatRepository.getCurrentUserName();
                long timestamp = System.currentTimeMillis();

                // Aquí sí mandamos la URL de la imagen
                Message message = new Message("", senderId, senderName, "", timestamp, imageUrl);
                chatRepository.sendMessage(chatId, message);
                
                // Indicamos que terminó de subir
                uploadState.setValue(false);
            }

            @Override
            public void onError(String error) {
                // Indicamos que terminó (con error)
                uploadState.setValue(false);
            }
        });
    }

    public LiveData<List<Message>> getMessages(String chatId) {
        // Para evitar crear multiples listeners, solo inicializamos si es nulo
        if (messagesLiveData == null) {
            messagesLiveData = chatRepository.getMessages(chatId);
        }
        return messagesLiveData;
    }
}