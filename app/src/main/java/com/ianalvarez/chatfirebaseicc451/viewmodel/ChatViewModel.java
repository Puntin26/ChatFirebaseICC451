package com.ianalvarez.chatfirebaseicc451.viewmodel;

import androidx.lifecycle.LiveData;
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

    public LiveData<List<Message>> getMessages(String chatId) {
        // Para evitar crear multiples listeners, solo inicializamos si es nulo
        if (messagesLiveData == null) {
            messagesLiveData = chatRepository.getMessages(chatId);
        }
        return messagesLiveData;
    }
}